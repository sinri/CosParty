# Conversation Package Documentation

## 概述

`conversation` 包提供了在AI大模型环境下进行多角色对话的核心功能。该包设计用于支持角色扮演（Cosplay）场景，允许多个角色在同一个对话上下文中进行交互。

## 核心组件

### 1. Actor（角色）

**文件**: `Actor.java`

**功能**: 定义对话系统中的参与角色，每个角色具有唯一的名称和特定的行为指令。

**主要特性**:
- **角色名称** (`actorName`): 角色的唯一标识符
- **角色指令** (`actorInstruction`): 描述角色的行为模式、性格特征或特定任务
- **JSON序列化**: 实现了 `JsonifiableEntity` 接口，支持数据的持久化和传输

**核心方法**:
- `getActorName()` / `setActorName()`: 获取/设置角色名称
- `getActorInstruction()` / `setActorInstruction()`: 获取/设置角色指令
- `toJsonObject()`: 序列化为JSON格式
- `reloadDataFromJsonObject()`: 从JSON反序列化

**使用示例**:
```java
Actor miku = new Actor()
    .setActorName("初音未来")
    .setActorInstruction("你是一个虚拟歌手，性格活泼可爱，喜欢唱歌和与粉丝互动");
```

### 2. Speech（发言）

**文件**: `Speech.java`

**功能**: 记录对话中单个角色的发言内容，包含发言者和具体内容。

**主要特性**:
- **发言者** (`actorName`): 发言角色的名称，与 `Actor` 的 `actorName` 保持一致
- **发言内容** (`content`): 角色发表的具体文本内容
- **JSON序列化**: 支持序列化和反序列化操作

**核心方法**:
- `getActorName()` / `setActorName()`: 获取/设置发言者名称
- `getContent()` / `setContent()`: 获取/设置发言内容
- `toJsonObject()`: 转换为JSON格式
- `reloadDataFromJsonObject()`: 从JSON恢复数据

**使用示例**:
```java
Speech speech = new Speech()
    .setActorName("初音未来")
    .setContent("大家好！我是初音未来，很高兴见到你们！");
```

### 3. Conversation（对话）

**文件**: `Conversation.java`

**功能**: 管理一场连续的对话，包含多个按时间顺序排列的发言。

**主要特性**:
- **对话代码** (`conversationCode`): 对话的唯一标识符，自动生成UUID
- **发言列表** (`speechList`): 按时间顺序存储的发言集合
- **线程安全**: 该类不是线程安全的，多线程环境下需要外部同步

**核心方法**:
- `getConversationCode()`: 获取对话代码
- `addSpeech()`: 向对话中添加新发言
- `getIterableOfSpeechList()`: 获取发言列表的迭代器
- `toJsonObject()`: 序列化整个对话
- `reloadDataFromJsonObject()`: 从JSON恢复对话数据

**使用示例**:
```java
Conversation conversation = new Conversation();
conversation.addSpeech(speech1)
    .addSpeech(speech2)
    .addSpeech(speech3);
```

### 4. ConversationContext（对话上下文）

**文件**: `ConversationContext.java`

**功能**: 管理同一群角色进行的若干场对话，提供角色和对话的统一管理。

**主要特性**:
- **角色管理**: 使用 `TreeMap` 存储角色，保证角色名称的有序性
- **对话管理**: 使用 `HashMap` 存储对话，提高查找效率
- **上下文索引**: 为对话上下文分配唯一标识，用于在整体流程中定位

**核心方法**:
- `registerActor()`: 注册新角色
- `registerConversation()`: 注册新对话
- `getActor()`: 根据名称获取角色
- `getConversation()`: 根据代码获取对话
- `getActors()`: 获取所有角色列表
- `setConversationContextIndex()`: 设置上下文索引（只能设置一次）

**使用示例**:
```java
ConversationContext context = new ConversationContext()
    .setConversationContextIndex(1)
    .registerActor(miku)
    .registerActor(kaito)
    .registerConversation(conversation);
```

## 类关系图

```
ConversationContext
    ├── Actor (TreeMap<String, Actor>)
    │   └── actorName: String
    │   └── actorInstruction: String
    │
    └── Conversation (HashMap<String, Conversation>)
        ├── conversationCode: String
        └── speechList: List<Speech>
            ├── actorName: String
            └── content: String
```

## 数据流

1. **角色定义**: 通过 `Actor` 类定义参与对话的角色
2. **角色注册**: 将角色注册到 `ConversationContext` 中
3. **发言创建**: 通过 `Speech` 类创建角色的发言
4. **对话构建**: 将发言添加到 `Conversation` 中
5. **对话注册**: 将对话注册到 `ConversationContext` 中
6. **数据持久化**: 通过JSON序列化保存对话数据

## 设计模式

- **组合模式**: `ConversationContext` 组合管理多个 `Actor` 和 `Conversation`
- **链式调用**: 所有setter方法都返回当前实例，支持链式调用
- **JSON序列化**: 所有核心类都实现了 `JsonifiableEntity` 接口

## 使用场景

1. **角色扮演游戏**: 多个AI角色在虚拟环境中进行对话
2. **对话系统**: 构建复杂的多轮对话流程
3. **数据持久化**: 保存和恢复对话历史
4. **对话分析**: 分析角色间的交互模式

## 注意事项

1. **线程安全**: `Conversation` 类不是线程安全的，多线程环境下需要外部同步
2. **索引唯一性**: `ConversationContext` 的索引只能设置一次
3. **角色名称一致性**: `Speech` 中的 `actorName` 必须与已注册的 `Actor` 名称保持一致
4. **UUID生成**: `Conversation` 的对话代码使用UUID自动生成，确保唯一性
