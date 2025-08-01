package io.github.sinri.CosParty.kernel;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

/**
 * 角色扮演场景接口，定义角色扮演系统中的基本执行单元。
 * <p>
 * 场景是角色扮演系统的最小执行单元，代表一个具体的业务逻辑片段。
 * 每个场景都有唯一的场景代码（sceneCode）和实例ID（instanceId），
 * 用于在脚本执行过程中进行标识和追踪。
 * <p>
 * 场景的生命周期包括初始化（initialize）和执行（play）两个阶段：
 * <ul>
 *   <li>初始化阶段：场景被引擎加载时调用，用于设置场景的初始状态</li>
 *   <li>执行阶段：场景被引擎执行时调用，实现具体的业务逻辑</li>
 * </ul>
 * <p>
 * 场景可以是简单的执行单元，也可以是复杂的动作（如 {@link CosplayAction}）。
 * 复杂的动作可以包含多个子场景，形成嵌套的场景结构。
 * <p>
 * 引擎（{@link CosplayEngine}）负责管理场景的执行流程，根据脚本（{@link CosplayScript}）
 * 定义的逻辑来决定场景的执行顺序和跳转。
 *
 * @see CosplayEngine
 * @see CosplayScript
 * @see CosplayAction
 * @since 1.0
 */
public interface CosplayScene {
    /**
     * 获取场景的唯一标识代码。
     * <p>
     * 场景代码用于在脚本范围内唯一标识一个场景，通常使用类的完全限定名。
     * 引擎使用此代码来查找和跳转到指定的场景。
     * <p>
     * 此方法在以下场景中被调用：
     * <ul>
     *   <li>引擎确定下一个要执行的场景时</li>
     *   <li>动作（CosplayAction）查找子场景时</li>
     *   <li>脚本管理场景跳转时</li>
     * </ul>
     * <p>
     * 实现类应确保返回的场景代码在脚本范围内唯一，且与 {@link CosplayScript#getSceneByCodeInScript(String)}
     * 方法中的场景代码保持一致。
     *
     * @return 场景的唯一标识代码，不能为null
     */
    @Nonnull
    String getSceneCode();

    /**
     * 获取场景的实例ID。
     * <p>
     * 实例ID用于区分同一场景类的不同实例，通常由场景代码和随机UUID组成。
     * 每个场景实例都有唯一的实例ID，用于日志记录和调试追踪。
     * <p>
     * 此方法主要用于：
     * <ul>
     *   <li>日志记录时标识具体的场景实例</li>
     *   <li>调试时追踪场景的执行状态</li>
     *   <li>场景实例的生命周期管理</li>
     * </ul>
     * <p>
     * 实现类应确保每个实例都有唯一的ID，通常的实现方式是在构造函数中生成。
     *
     * @return 场景实例的唯一ID，不能为null
     */
    @Nonnull
    String getInstanceId();

    /**
     * 初始化场景。
     * <p>
     * 当场景被引擎加载时调用此方法进行初始化。初始化阶段用于：
     * <ul>
     *   <li>设置场景的初始状态</li>
     *   <li>准备执行所需的资源</li>
     *   <li>验证场景的执行条件</li>
     * </ul>
     * <p>
     * 此方法在以下时机被调用：
     * <ul>
     *   <li>引擎启动时，对起始场景进行初始化</li>
     *   <li>场景跳转时，对新场景进行初始化</li>
     *   <li>动作（CosplayAction）被推入动作栈时</li>
     * </ul>
     * <p>
     * 如果初始化失败，引擎会停止执行并记录错误。成功的初始化应返回 {@link Future#succeededFuture()}。
     * 对于不需要特殊初始化的场景，可以返回立即完成的Future。
     *
     * @param engine 执行引擎实例，提供上下文和日志等基础设施
     * @return 初始化完成的Future，成功时返回succeededFuture，失败时返回failedFuture
     */
    Future<Void> initialize(@Nonnull CosplayEngine engine);

    /**
     * 执行场景的核心业务逻辑。
     * <p>
     * 这是场景的主要执行方法，包含场景要完成的具体业务逻辑。
     * 引擎会调用此方法来执行场景，并根据执行结果决定后续的流程。
     * <p>
     * 此方法在以下时机被调用：
     * <ul>
     *   <li>引擎的主执行循环中，对当前场景进行执行</li>
     *   <li>场景执行完成后，引擎会调用 {@link CosplayScript} 的相关方法来确定下一个场景</li>
     * </ul>
     * <p>
     * 执行过程中，场景可以：
     * <ul>
     *   <li>读取和修改上下文（{@link CosplayContext}）中的数据</li>
     *   <li>记录执行日志</li>
     *   <li>执行异步操作（如API调用、数据库操作等）</li>
     *   <li>与其他系统组件交互</li>
     * </ul>
     * <p>
     * 执行成功应返回 {@link Future#succeededFuture()}，失败应返回包含异常信息的failedFuture。
     * 引擎会根据执行结果来决定是否继续执行下一个场景。
     *
     * @param engine 执行引擎实例，提供上下文、日志和场景管理功能
     * @return 执行完成的Future，成功时返回succeededFuture，失败时返回failedFuture
     */
    @Nonnull
    Future<Void> play(@Nonnull CosplayEngine engine);

}
