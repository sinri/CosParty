package io.github.sinri.CosParty.facade;

import io.vertx.core.Future;

import javax.annotation.Nonnull;

/**
 * 大模型工作流中的最细粒度处理单元
 * <p>
 * 在基于大语言模型的角色扮演工作流中，场景是最小的可执行单元，负责处理：
 * <ul>
 *   <li>单轮对话交互</li>
 *   <li>上下文状态管理</li>
 *   <li>AI响应的生成和处理</li>
 *   <li>工作流的下一步决策</li>
 * </ul>
 * <p>
 * 每个场景代表工作流中的一个具体步骤，可以包含：
 * <ul>
 *   <li>向大模型发送提示词</li>
 *   <li>处理AI生成的回复</li>
 *   <li>更新对话上下文</li>
 *   <li>决定后续处理路径</li>
 * </ul>
 * <p>
 * 实现此接口的类应该：
 * <ul>
 *   <li>定义明确的处理边界和职责</li>
 *   <li>处理与大模型的交互逻辑</li>
 *   <li>管理场景相关的上下文数据</li>
 *   <li>返回下一个处理单元的标识</li>
 * </ul>
 */
public interface CosplayScene {
    /**
     * 获取处理单元的唯一标识符
     * <p>
     * 在工作流中唯一标识一个处理单元，用于：
     * <ul>
     *   <li>工作流的路由和跳转</li>
     *   <li>处理单元的状态追踪</li>
     *   <li>调试和日志记录</li>
     *   <li>工作流的暂停和恢复</li>
     * </ul>
     * <p>
     * 建议使用描述性的标识符，例如：
     * <ul>
     *   <li>"user_input_processing" - 用户输入处理</li>
     *   <li>"ai_response_generation" - AI响应生成</li>
     *   <li>"context_update" - 上下文更新</li>
     *   <li>"decision_making" - 决策制定</li>
     * </ul>
     *
     * @return 处理单元的唯一标识符，不能为null
     */
    @Nonnull
    String getSceneCode();

    /**
     * 执行当前处理单元的逻辑
     * <p>
     * 执行大模型工作流中的一个具体步骤，包括：
     * <ul>
     *   <li>构建发送给大模型的提示词</li>
     *   <li>调用大模型API获取响应</li>
     *   <li>解析和处理AI生成的回复</li>
     *   <li>更新工作流上下文和状态</li>
     *   <li>决定下一个处理单元</li>
     * </ul>
     * <p>
     * 处理完成后，返回下一个要执行的处理单元标识。如果返回null，表示工作流结束。
     * <p>
     * 注意：此方法是异步的，使用Vert.x的Future来处理异步操作，
     * 避免阻塞事件循环线程，特别适合处理大模型API的异步调用。
     *
     * @param cosplayEngine 工作流引擎实例，提供大模型交互和上下文管理支持
     *                      <p>
     *                      引擎负责：
     *                      <ul>
     *                        <li>管理与大模型的连接和API调用</li>
     *                        <li>维护工作流的全局上下文和状态</li>
     *                        <li>处理处理单元间的数据传递</li>
     *                        <li>提供工具函数和服务（如日志、配置等）</li>
     *                        <li>处理错误和异常情况</li>
     *                      </ul>
     * @return 下一个处理单元标识的Future，不能为null
     *         <p>
     *         返回的标识应该对应工作流中定义的有效处理单元，
     *         如果标识无效，引擎应该处理相应的错误情况。
     */
    @Nonnull
    Future<String> play(@Nonnull CosplayEngine cosplayEngine);

}
