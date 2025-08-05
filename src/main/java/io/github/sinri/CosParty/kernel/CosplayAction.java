package io.github.sinri.CosParty.kernel;

import io.github.sinri.CosParty.kernel.context.CosplayContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 角色扮演动作接口。
 * <p>
 * 动作是包含多个子场景的复杂场景，具有独立的上下文作用域。
 * 支持从外层上下文输入数据，执行完成后输出数据到外层上下文。
 */
public interface CosplayAction extends CosplayScene {

    @Nonnull
    String getMappedFieldNameInOuterScope(@Nonnull String actionContextFieldName);

    /**
     * 从脚本上下文拉取数据到动作上下文。
     *
     * @param outerScopeContext 脚本作用域上下文
     */
    void input(@Nonnull CosplayContext outerScopeContext);

    /**
     * 将动作上下文数据推送到脚本上下文。
     *
     * @param outerScopeContext 脚本作用域上下文
     */
    void output(@Nonnull CosplayContext outerScopeContext);

    /**
     * As action holds its own independent context...
     *
     * @return the context on action scope.
     */
    @Nonnull
    CosplayContext context() throws IllegalStateException;

    @Nullable
    String seekNextSceneInAction(@Nonnull String currentSceneCode);

    @Nonnull
    CosplayScene getSceneByCodeInAction(@Nonnull String nextSceneCodeInAction);

}
