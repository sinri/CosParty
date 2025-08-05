package io.github.sinri.CosParty.miku.two;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.CosParty.miku.MikuScript;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DiscussionScript extends MikuScript implements DiscussionContextMixin {

    private CosplayContext contextOnScriptScope;

    @Override
    public CosplayContext context() throws IllegalStateException {
        if (contextOnScriptScope == null) throw new IllegalStateException();
        return contextOnScriptScope;
    }

    @Nullable
    @Override
    public String seekNextSceneInScript(@Nonnull CosplayContext contextOnScriptScope, @Nonnull String currentSceneCode) {
        this.contextOnScriptScope = contextOnScriptScope;
        if (currentSceneCode.equals(StartScene.class.getName())) {
            return OneRoundScene.class.getName();
        } else if (currentSceneCode.equals(OneRoundScene.class.getName())) {
            return AfterOneRoundScene.class.getName();
        } else if (currentSceneCode.equals(AfterOneRoundScene.class.getName())) {
            Integer endFlag = endFlag();
            if (endFlag != null && endFlag == 1) {
                return EndScene.class.getName();
            } else {
                return OneRoundScene.class.getName();
            }
        } else if (currentSceneCode.equals(EndScene.class.getName())) {
            return null;
        }
        throw new IllegalStateException();
    }
}
