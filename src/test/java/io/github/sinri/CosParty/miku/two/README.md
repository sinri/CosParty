# CosParty 讨论工作流教程

## 概述

本教程介绍如何使用 CosParty 框架构建一个多角色讨论系统。该工作流模拟了一个技术讨论场景，包含主持人引导、多轮讨论和总结等环节。

## 工作流架构

### 核心组件

1. **DiscussionScript** - 定义讨论脚本和字段常量
2. **StartScene** - 初始化讨论环境和参与者
3. **OneRoundScene** - 执行一轮讨论，让所有参与者发言
4. **AfterOneRoundScene** - 主持人分析讨论进展，决定是否继续
5. **EndScene** - 总结讨论并形成结论

### 数据流

```
Discussion.java (入口)
    ↓
DiscussionScript (脚本定义)
    ↓
StartScene (初始化)
    ↓
OneRoundScene (第一轮讨论)
    ↓
AfterOneRoundScene (主持人判断)
    ↓
OneRoundScene (继续讨论) 或 EndScene (结束)
```

## 详细实现

### 1. 脚本定义 (DiscussionScript.java)

```java
public class DiscussionScript extends MikuScript {
    public static final String FIELD_TOPIC = "topic";                    // 讨论主题
    public static final String FIELD_MEMBERS = "members";                // 参与者列表
    public static final String FIELD_CONVERSATION_CONTEXT_ID = "conversation_context_id";  // 对话上下文ID
    public static final String FIELD_CONVERSATION_CODE = "conversation_code";              // 对话代码
    public static final String FIELD_ROUND_COUNT = "round_count";        // 讨论轮次计数
}
```

### 2. 启动场景 (StartScene.java)

**功能**: 初始化讨论环境，注册参与者和主持人

**关键步骤**:
- 读取讨论主题和参与者信息
- 创建对话上下文 (ConversationContext)
- 注册主持人角色
- 注册所有参与者角色
- 创建初始对话并添加主持人开场白
- 返回下一场景 (OneRoundScene)

**核心代码**:
```java
// 创建对话上下文
var conversationContext = cosplayContext.createConversationContext();

// 注册主持人
Actor actorHost = new Actor()
    .setActorName("主持人")
    .setActorInstruction("围绕议题主持讨论，在各方充分发表意见后进行分析总结，并给出自己的结论。");
conversationContext.registerActor(actorHost);

// 注册参与者
array.forEach(item -> {
    var actor = new Actor().reloadDataFromJsonObject((JsonObject) item);
    conversationContext.registerActor(actor);
});
```

### 3. 讨论轮次 (OneRoundScene.java)

**功能**: 让所有参与者轮流发言

**关键步骤**:
- 获取对话上下文和参与者列表
- 遍历所有参与者（跳过主持人）
- 为每个参与者调用 AI 服务生成发言内容
- 将发言添加到对话历史
- 更新讨论轮次计数
- 返回下一场景 (AfterOneRoundScene)

**AI 调用逻辑**:
```java
// 为每个参与者生成发言
return mixChatKit.chatStream(SupportedModelEnum.QwenPlus, req -> {
    // 设置角色身份
    req.addMessage(msg -> msg
        .setRole("system")
        .setTextContent("你是%s，%s\n你正在参与这场主题讨论。"
            .formatted(actor.getActorName(), actor.getActorInstruction()))
    );
    
    // 提供讨论历史
    req.addMessage(msg -> msg
        .setRole("user")
        .setTextContent("至今为止的讨论发言记录...\n现在论到你发言了。")
    );
});
```

### 4. 轮次后分析 (AfterOneRoundScene.java)

**功能**: 主持人分析讨论进展，决定是否继续

**关键步骤**:
- 检查讨论轮次（最多4轮）
- 主持人分析当前讨论内容
- 判断是否需要继续讨论
- 根据判断返回下一场景

**判断逻辑**:
```java
// 主持人分析讨论进展
return mixChatKit.chatStream(SupportedModelEnum.QwenTurbo, req -> {
    req.addMessage(msg -> msg
        .setRole("user")
        .setTextContent("""
            %s
            作为主持人，你需要分析以上发言，判断是否还有未提及或值得讨论的内容，选择推进话题或准备结束讨论。
            如果你发现还有需要讨论的内容，请输出"继续讨论"并指出进一步讨论的方向。
            如果你觉得讨论已经足够充分，请输出"结束讨论"。
            """.formatted(history.toString()))
    );
});
```

### 5. 结束场景 (EndScene.java)

**功能**: 总结讨论并形成结论

**关键步骤**:
- 获取完整的讨论历史
- 主持人进行总结分析
- 形成最终结论
- 结束工作流

## 使用方法

### 1. 创建工作流实例

```java
public class Discussion extends Privateer {
    @Override
    protected Future<Void> launchAsPrivateer() {
        // 创建脚本
        DiscussionScript script = new DiscussionScript();
        script.addScene(StartScene.class)
              .addScene(OneRoundScene.class)
              .addScene(AfterOneRoundScene.class)
              .addScene(EndScene.class);
        script.confirmStartScene(StartScene.class);

        // 创建引擎
        MikuEngine engine = new MikuEngine(script);
        
        // 设置初始参数
        return engine.swift(Map.of(
            DiscussionScript.FIELD_TOPIC, "讨论主题",
            DiscussionScript.FIELD_MEMBERS, new JsonArray()
                .add(new Actor()
                    .setActorName("角色1")
                    .setActorInstruction("角色描述"))
                .add(new Actor()
                    .setActorName("角色2")
                    .setActorInstruction("角色描述"))
                .toString()
        ));
    }
}
```

### 2. 自定义参与者

每个参与者需要定义：
- **actorName**: 角色名称
- **actorInstruction**: 角色描述和特点

示例：
```java
new Actor()
    .setActorName("老黑")
    .setActorInstruction("拥有很长的企业项目开发经验，特别关注系统稳定性。")
```

### 3. 运行工作流

```java
Discussion discussion = new Discussion();
discussion.launchAsPrivateer();
```

## 扩展建议

### 1. 自定义讨论主题

修改 `Discussion.java` 中的 `FIELD_TOPIC` 参数：
```java
DiscussionScript.FIELD_TOPIC, "你的讨论主题"
```

### 2. 调整讨论轮次

在 `AfterOneRoundScene.java` 中修改轮次限制：
```java
if (Objects.requireNonNullElse(roundCount, 0) > 3) {  // 修改数字
    return Future.succeededFuture(EndScene.class.getName());
}
```

### 3. 添加新的参与者类型

在 `Discussion.java` 中添加更多参与者：
```java
.add(new Actor()
    .setActorName("新角色")
    .setActorInstruction("新角色的特点和观点"))
```

### 4. 自定义 AI 模型

在各个场景中修改使用的 AI 模型：
```java
SupportedModelEnum.QwenPlus    // 参与者发言
SupportedModelEnum.QwenTurbo   // 主持人分析
SupportedModelEnum.QwenMax     // 最终总结
```

## 技术特点

1. **异步处理**: 使用 Vert.x 的 Future 进行异步操作
2. **上下文管理**: 通过 CosplayContext 管理全局状态
3. **对话历史**: 自动维护完整的讨论记录
4. **角色扮演**: 每个参与者都有独特的身份和特点
5. **智能判断**: 主持人能够智能分析讨论进展

## 注意事项

1. 确保 AI 服务配置正确
2. 参与者角色描述要清晰明确
3. 讨论主题要具体且有争议性
4. 注意控制讨论轮次，避免过长
5. 监控 AI 调用频率和成本

这个工作流展示了 CosParty 框架在构建复杂对话系统方面的强大能力，可以轻松扩展到其他类型的多角色交互场景。
