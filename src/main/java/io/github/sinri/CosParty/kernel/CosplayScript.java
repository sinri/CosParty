package io.github.sinri.CosParty.kernel;

import io.github.sinri.CosParty.kernel.context.CosplayContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 角色扮演脚本接口。
 * <p>
 * 定义角色扮演场景的执行流程和场景管理，包含多个场景和跳转逻辑。
 */
public interface CosplayScript {
    /**
     * 获取脚本的起始场景。
     *
     * @return 起始场景实例
     */
    @Nonnull
    CosplayScene getStartingScene();

    /**
     * 根据场景代码获取脚本中对应的场景实例。
     *
     * @param sceneCode 场景代码
     * @return 对应的场景实例
     */
    @Nonnull
    CosplayScene getSceneByCodeInScript(@Nonnull String sceneCode);

    /**
     * 根据当前场景代码和上下文状态，确定脚本中的下一个场景。
     *
     * @param contextOnScriptScope 脚本级别的上下文
     * @param currentSceneCode     当前场景的代码
     * @return 下一个场景的代码，如果脚本结束则返回null
     */
    @Nullable
    String seekNextSceneInScript(@Nonnull CosplayContext contextOnScriptScope, @Nonnull String currentSceneCode);
}
