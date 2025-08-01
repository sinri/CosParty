package io.github.sinri.CosParty.kernel;

import io.github.sinri.CosParty.kernel.context.CosplayContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CosplayAction extends CosplayScene {

    @Nonnull
    String getMappedFieldNameInOuterScope(@Nonnull String actionContextFieldName);

    /**
     * Pull data from script context to action context as input, when the action starting.
     *
     * @param outerScopeContext context of script scope maintained by engine
     */
    void input(@Nonnull CosplayContext outerScopeContext);

    /**
     * Push data from action context to script context as output, when the action ending.
     *
     * @param outerScopeContext context of script scope maintained by engine
     */
    void output(@Nonnull CosplayContext outerScopeContext);


    @Nonnull
    CosplayContext getContextOnThisActionScope();

    @Nullable
    String seekNextSceneInAction(@Nonnull String currentSceneCode);

    @Nonnull
    CosplayScene getSceneByCodeInAction(@Nonnull String nextSceneCodeInAction);

}
