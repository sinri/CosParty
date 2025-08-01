package io.github.sinri.CosParty.miku.one;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.CosParty.miku.MikuScript;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SampleScript extends MikuScript {
    public SampleScript() {
        super();

        this.addScene(SceneStart.class);
        this.addScene(SceneJudge.class);
        this.confirmStartScene(SceneStart.class);
    }

    @Nullable
    @Override
    public String seekNextSceneInScript(@Nonnull CosplayContext contextOnScriptScope, @Nonnull String currentSceneCode) {
        if (currentSceneCode.equals(SceneStart.class.getName())) {
            return SceneJudge.class.getName();
        } else if (currentSceneCode.equals(SceneJudge.class.getName())) {
            return null;
        } else {
            throw new IllegalStateException();
        }
    }
}
