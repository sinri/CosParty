# CosParty 初音未来问答系统使用指南

## 示例功能概述

本示例实现了一个基于 CosParty 框架的初音未来角色扮演问答系统。系统工作流程如下：

1. **用户提问** → 系统接收用户的中文问题
2. **初音回答** → 以初音未来身份用日语回答用户问题  
3. **语法检查与翻译** → 对日语回答进行语法检查并翻译成中文
4. **输出结果** → 返回最终的中文翻译结果

## 框架核心机制

### 1. 三层架构设计

CosParty 采用三层架构来组织 AI 工作流：

- **引擎层（MikuEngine）**：负责执行整个工作流，管理场景生命周期
- **脚本层（MikuScript）**：定义场景执行顺序和跳转逻辑
- **场景层（MikuScene）**：实现具体的业务逻辑单元

### 2. 场景驱动机制

每个场景都是一个独立的业务逻辑单元，具有以下特点：

- **单一职责**：每个场景只负责一个特定的功能
- **数据传递**：通过上下文对象在场景间传递数据
- **动态跳转**：支持条件跳转和顺序执行
- **生命周期管理**：引擎自动管理场景的创建、初始化和销毁

### 3. 上下文数据管理

使用 `CosplayContext` 对象在场景间传递数据：

- 支持字符串、数字、布尔值等基本类型
- 数据在整个工作流生命周期内保持有效
- 提供类型安全的读写操作

## 实现要点

### 1. 场景类设计

每个场景类必须：
- 继承 `MikuScene` 基类
- 实现无参构造函数
- 重写 `playInner()` 方法实现业务逻辑
- 通过 `getCurrentContext()` 访问上下文
- 通过 `getLogger()` 记录日志

### 2. 脚本类设计

脚本类需要：
- 继承 `MikuScript` 基类
- 在构造函数中注册所有场景
- 设置起始场景
- 实现 `seekNextSceneInScript()` 方法定义跳转逻辑

### 3. 引擎启动

通过 `MikuEngine` 启动工作流：
- 创建脚本实例
- 创建引擎实例
- 调用 `startup()` 方法并传入初始参数

## 关键机制详解

### 1. 场景注册机制

场景必须在脚本中显式注册才能被使用：

```java
this.addScene(SceneStart.class);
this.addScene(SceneJudge.class);
```

**注意事项**：
- 未注册的场景无法被引擎加载
- 场景类名作为唯一标识符
- 重复注册会抛出异常

### 2. 场景跳转控制

通过 `seekNextSceneInScript()` 方法控制场景跳转：

```java
if (currentSceneCode.equals(SceneStart.class.getName())) {
    return SceneJudge.class.getName();
} else if (currentSceneCode.equals(SceneJudge.class.getName())) {
    return null; // 结束工作流
}
```

**注意事项**：
- 返回 `null` 表示工作流结束
- 返回的场景类名必须在脚本中已注册
- 未处理的场景代码会抛出异常

### 3. 上下文数据操作

场景间通过上下文传递数据：

```java
// 写入数据
getCurrentContext().writeString("first_answer", textContent);

// 读取数据  
var firstAnswer = getCurrentContext().readString("first_answer");
```

**注意事项**：
- 读取不存在的键会抛出异常
- 数据类型必须匹配
- 数据在整个工作流中保持有效

## 常见陷阱与解决方案

### 1. 场景注册陷阱

**问题**：忘记注册场景导致运行时异常
```java
// 错误：未注册场景
this.addScene(SceneStart.class);
// 忘记注册 SceneJudge.class
```

**解决**：确保所有场景都在构造函数中注册

### 2. 跳转逻辑陷阱

**问题**：跳转逻辑不完整导致异常
```java
// 错误：未处理所有场景
if (currentSceneCode.equals(SceneStart.class.getName())) {
    return SceneJudge.class.getName();
}
// 缺少 SceneJudge 的处理逻辑
```

**解决**：为每个场景提供明确的跳转逻辑

### 3. 数据传递陷阱

**问题**：读取未写入的数据
```java
// 错误：在 SceneJudge 中读取 first_answer，但 SceneStart 可能失败
var firstAnswer = getCurrentContext().readString("first_answer");
```

**解决**：添加数据存在性检查或异常处理

### 4. 异步操作陷阱

**问题**：在异步操作中访问上下文
```java
// 错误：在异步回调中直接访问上下文
return mixChatKit.chatStream(...)
    .compose(resp -> {
        // 这里的上下文可能已经失效
        getCurrentContext().writeString("result", resp.getMessage().getTextContent());
        return Future.succeededFuture();
    });
```

**解决**：在异步操作前保存必要的数据

## 最佳实践

### 1. 场景设计原则

- **职责单一**：每个场景只做一件事
- **数据明确**：明确定义输入输出数据
- **错误处理**：在场景中处理可能的异常
- **日志记录**：记录关键操作和错误信息

### 2. 脚本设计原则

- **逻辑清晰**：跳转逻辑要简单明确
- **场景完整**：确保所有场景都被正确处理
- **异常安全**：处理异常情况下的跳转

### 3. 数据管理原则

- **命名规范**：使用有意义的键名
- **类型一致**：保持数据类型的一致性
- **生命周期**：考虑数据的生命周期管理

## 运行示例

```bash
# 在项目根目录执行测试
mvn test -Dtest=MikuShow
```

## 扩展建议

1. **添加错误处理场景**：处理 AI 服务调用失败的情况
2. **增加条件跳转**：根据回答质量决定是否需要进一步处理
3. **优化性能**：考虑并行执行某些场景
4. **增强监控**：添加更详细的日志和指标收集

通过这个示例，你可以掌握 CosParty 框架的核心概念和使用方法，为构建更复杂的 AI 工作流打下基础。
