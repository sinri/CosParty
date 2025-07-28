package io.github.sinri.CosParty.miku;

import javax.annotation.Nonnull;

public class SampleMikuScript extends MikuScript {
    public final static String startingSceneCode = "STARTING_SCENE";
    public final static String judgeSceneCode = "JUDGE_SCENE";

    public SampleMikuScript(@Nonnull String rawQuestion) {
        super(new SceneStart(rawQuestion));
        this.addScene(new SceneJudge());
    }
}
