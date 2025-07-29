# 使用 CosParty 构建 AI 工作流 - 初音未来问答系统案例

## 案例介绍

本案例展示如何使用 CosParty 第三方库构建一个初音未来（Hatsune Miku）角色扮演问答系统。用户提问后，系统会：
1. 以初音未来的身份用日语回答
2. 对回答进行语法检查和中文翻译

通过这个具体案例，你将学会如何使用 CosParty 的核心概念来构建自己的 AI 工作流。

## 核心概念解析

### 什么是 CosParty？

CosParty 是一个用于构建 AI 工作流的 Java 框架，它将复杂的工作流分解为多个**场景（Scene）**，每个场景负责特定的业务逻辑，场景之间通过**上下文（Context）**进行数据传递。

### 核心实体关系

```
MikuEngine (引擎)
    ↓ 执行
MikuScript (脚本) 
    ↓ 编排
MikuScene (场景) 
    ↓ 传递数据
CosplayContext (上下文)
```

## 案例实现详解

### 第一步：创建入口类

```java
public class MikuShow extends Privateer {
    @Override
    protected Future<Void> launchAsPrivateer() {
        // 1. 创建脚本，定义工作流
        MikuScript mikuScript = new MikuScript();
        mikuScript.addScene(SceneStart.class);
        mikuScript.addScene(SceneJudge.class);
        mikuScript.confirmStartScene(SceneStart.class);

        // 2. 创建引擎并执行
        MikuEngine engine = new MikuEngine(mikuScript);
        return engine.swift(Map.of("raw_question", "千本樱这个歌是讲什么东西的，里面有什么典型片段"));
    }
}
```

**关键概念解释：**
- `MikuScript`：工作流脚本，定义场景顺序和起始场景
- `MikuEngine`：执行引擎，负责按脚本编排执行各个场景
- `engine.swift()`：启动工作流，传入初始参数

### 第二步：实现场景类

#### SceneStart - 第一个场景

```java
class SceneStart extends MikuScene {
    @Override
    protected Future<String> playInner(
            CosplayContext cosplayContext, 
            KeelIssueRecorder<KeelEventLog> logger
    ) {
        // 1. 读取输入参数
        var rawQuestion = cosplayContext.readString("raw_question");
        
        // 2. 调用 AI 服务
        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);
        
        return mixChatKit.chatStream(
                SupportedModelEnum.QwenPlus,
                req -> req
                        .addMessage(msg -> msg
                                .setRole("system")
                                .setTextContent("You are Hatsune Miku. Answer the questions from your fans in Japanese.")
                        )
                        .addMessage(msg -> msg
                                .setRole("user")
                                .setTextContent(rawQuestion)
                        )
        )
        .compose(resp -> {
            // 3. 处理结果并存储到上下文
            String textContent = resp.getMessage().getTextContent();
            cosplayContext.writeString("first_answer", textContent);
            logger.info("first_answer: " + textContent);
            
            // 4. 返回下一个场景类名
            return Future.succeededFuture(SceneJudge.class.getName());
        });
    }
}
```

**关键概念解释：**
- `MikuScene`：场景基类，所有场景都必须继承此类
- `playInner()`：场景的核心逻辑方法
- `CosplayContext`：上下文对象，用于场景间数据传递
- `cosplayContext.readString()`：读取上一个场景传递的数据
- `cosplayContext.writeString()`：将数据存储到上下文，供下一个场景使用
- 返回值：返回下一个场景的类名，实现场景跳转

#### SceneJudge - 第二个场景

```java
class SceneJudge extends MikuScene {
    @Override
    protected Future<String> playInner(
            CosplayContext cosplayContext, 
            KeelIssueRecorder<KeelEventLog> logger
    ) {
        // 1. 读取上一个场景的结果
        var firstAnswer = cosplayContext.readString("first_answer");
        
        // 2. 调用 AI 服务进行语法检查和翻译
        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);
        
        return mixChatKit.chatStream(
                SupportedModelEnum.QwenPlus,
                req -> req
                        .addMessage(msg -> msg
                                .setRole("system")
                                .setTextContent("根据用户的输入，检查语法并翻译成中文")
                        )
                        .addMessage(msg -> msg
                                .setRole("user")
                                .setTextContent(firstAnswer)
                        )
        )
        .compose(resp -> {
            // 3. 处理最终结果
            String textContent = resp.getMessage().getTextContent();
            logger.info("second_answer: " + textContent);
            cosplayContext.writeString("second_answer", textContent);
            
            // 4. 返回 null 表示工作流结束
            return Future.succeededFuture(null);
        });
    }
}
```

**关键概念解释：**
- 返回 `null`：表示工作流结束，不再跳转到下一个场景
- 数据流转：`raw_question` → `first_answer` → `second_answer`

## 如何构建自己的工作流

### 1. 设计工作流步骤

首先明确你的业务需求，将复杂流程分解为多个独立步骤：

```
用户输入 → 步骤1 → 步骤2 → ... → 最终结果
```

### 2. 创建场景类

为每个步骤创建一个继承 `MikuScene` 的类：

```java
public class YourScene extends MikuScene {
    @Override
    protected Future<String> playInner(
            CosplayContext cosplayContext, 
            KeelIssueRecorder<KeelEventLog> logger
    ) {
        // 1. 读取输入
        String input = cosplayContext.readString("input_key");
        
        // 2. 执行业务逻辑
        String result = processYourBusinessLogic(input);
        
        // 3. 存储结果
        cosplayContext.writeString("output_key", result);
        
        // 4. 返回下一个场景类名或 null
        return Future.succeededFuture(NextScene.class.getName());
    }
}
```

### 3. 组装工作流

```java
// 创建脚本
MikuScript script = new MikuScript();
script.addScene(Step1Scene.class);
script.addScene(Step2Scene.class);
script.addScene(Step3Scene.class);
script.confirmStartScene(Step1Scene.class);

// 创建引擎
MikuEngine engine = new MikuEngine(script);

// 执行工作流
engine.swift(Map.of("initial_param", "initial_value"));
```

### 4. 数据传递模式

CosParty 支持多种数据传递方式：

```java
// 字符串
cosplayContext.writeString("key", "value");
String value = cosplayContext.readString("key");

// 数字
cosplayContext.writeInteger("count", 42);
Integer count = cosplayContext.readInteger("count");

// 布尔值
cosplayContext.writeBoolean("flag", true);
Boolean flag = cosplayContext.readBoolean("flag");

// 对象（需要序列化）
cosplayContext.writeString("json_data", objectMapper.writeValueAsString(obj));
```

## 最佳实践

### 1. 场景设计原则

- **单一职责**：每个场景只负责一个特定的业务逻辑
- **数据明确**：明确定义场景间的数据传递接口
- **错误处理**：在场景中处理可能的异常情况

### 2. 命名规范

- 场景类名：`功能名 + Scene`，如 `UserLoginScene`
- 上下文键名：使用下划线分隔，如 `user_id`、`login_result`

### 3. 日志记录

```java
// 记录关键信息
logger.info("Processing user input: " + input);

// 记录错误
logger.error("Failed to process request", exception);
```

### 4. 场景跳转控制

```java
// 条件跳转
if (condition) {
    return Future.succeededFuture(SceneA.class.getName());
} else {
    return Future.succeededFuture(SceneB.class.getName());
}

// 结束工作流
return Future.succeededFuture(null);
```

## 运行示例

```bash
# 在项目根目录执行
mvn test -Dtest=MikuShow
```

## 扩展阅读

- 查看 `src/main/java/io/github/sinri/CosParty/miku/` 目录了解核心类实现
- 参考 `src/test/java/io/github/sinri/CosParty/miku/two/` 目录查看更复杂的工作流示例
- 探索 `src/main/java/io/github/sinri/CosParty/facade/` 目录了解框架的抽象层设计

通过这个案例，你已经学会了 CosParty 的核心概念和使用方法。现在可以开始构建你自己的 AI 工作流了！
