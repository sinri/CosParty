package io.github.sinri.CosParty.miku.three;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.CosParty.miku.MikuScript;
import io.github.sinri.CosParty.miku.three.action.DebateAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DebateScript extends MikuScript {
    public DebateScript() {
        super();
        this.addScene(RandomMotionScene.class);
        this.addScene(DebateAction.class);
        this.addScene(MotionConclusionScene.class);
        this.confirmStartScene(RandomMotionScene.class);
    }

    @Nullable
    @Override
    public String seekNextSceneInScript(@Nonnull CosplayContext contextOnScriptScope, @Nonnull String currentSceneCode) {
        if (currentSceneCode.equals(RandomMotionScene.class.getName())) {
            return DebateAction.class.getName();
        } else if (currentSceneCode.equals(DebateAction.class.getName())) {
            return MotionConclusionScene.class.getName();
        } else if (currentSceneCode.equals(MotionConclusionScene.class.getName())) {
            return null;
        }
        throw new IllegalStateException();
    }
}
