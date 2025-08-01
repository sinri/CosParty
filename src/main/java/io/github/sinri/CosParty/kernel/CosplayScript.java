package io.github.sinri.CosParty.kernel;

import io.github.sinri.CosParty.kernel.context.CosplayContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 角色扮演脚本接口，定义角色扮演场景的执行流程和场景管理。
 * <p>
 * 脚本是角色扮演系统的核心组件，负责定义场景的执行顺序和跳转逻辑。
 * 每个脚本包含多个场景（CosplayScene），场景可以是简单的执行单元或复杂的动作（CosplayAction）。
 * <p>
 * 脚本通过场景代码（sceneCode）来标识和管理不同的场景，引擎会根据当前场景的执行结果
 * 和上下文状态来决定下一个要执行的场景。
 * <p>
 * 实现类需要提供完整的场景定义和跳转逻辑，确保脚本能够正确执行并最终结束。
 *
 * @since 1.0
 */
public interface CosplayScript {
    /**
     * 获取脚本的起始场景。
     * <p>
     * 引擎启动时会首先调用此方法获取起始场景，然后开始执行脚本。
     * 起始场景是脚本执行的入口点，必须存在且有效。
     * <p>
     * 此方法在引擎初始化阶段被调用，用于确定脚本的执行起点。
     *
     * @return 起始场景实例，不能为null
     */
    @Nonnull
    CosplayScene getStartingScene();

    /**
     * 根据场景代码获取脚本中对应的场景实例。
     * <p>
     * 当引擎需要跳转到指定场景时，会调用此方法获取场景实例。
     * 场景代码是场景的唯一标识符，在脚本范围内必须唯一。
     * <p>
     * 此方法在场景跳转时被调用，用于根据场景代码定位具体的场景实现。
     * 如果场景代码不存在，实现类应抛出适当的异常。
     *
     * @param sceneCode 场景代码，用于标识场景
     * @return 对应的场景实例，不能为null
     * @throws IllegalArgumentException 当场景代码不存在时抛出
     */
    @Nonnull
    CosplayScene getSceneByCodeInScript(@Nonnull String sceneCode);

    /**
     * 根据当前场景代码和上下文状态，确定脚本中的下一个场景。
     * <p>
     * 当当前场景执行完成后，引擎（{@link CosplayEngine}）会调用此方法来确定下一个要执行的场景。
     * 方法会根据当前场景代码和脚本级别的上下文状态来判断跳转逻辑。
     *
     * <p>
     * 返回null表示脚本执行结束，引擎将停止执行。
     * 返回的场景代码必须对应脚本中存在的场景。
     * <p>
     * 此方法在场景执行完成后被调用，用于实现脚本级别的场景流转逻辑。
     * 实现类需要根据业务逻辑和上下文状态来决定下一个场景。
     *
     * @param contextOnScriptScope 脚本级别的上下文，包含脚本执行过程中的状态数据
     * @param currentSceneCode     当前场景的代码
     * @return 下一个场景的代码，如果脚本结束则返回null
     */
    @Nullable
    String seekNextSceneInScript(@Nonnull CosplayContext contextOnScriptScope, @Nonnull String currentSceneCode);
}
