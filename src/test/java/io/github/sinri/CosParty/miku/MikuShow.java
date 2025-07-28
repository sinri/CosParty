package io.github.sinri.CosParty.miku;

import io.github.sinri.drydock.naval.raider.Privateer;
import io.vertx.core.Future;

import java.util.Map;

public class MikuShow extends Privateer {
    private final static String startingSceneCode = "STARTING_SCENE";
    private final static String judgeSceneCode = "JUDGE_SCENE";

    @Override
    protected Future<Void> launchAsPrivateer() {
        // AigcMix.enableVerboseLogger();

        MikuScript mikuScript = new MikuScript();
        mikuScript.addScene(SceneStart.class);
        mikuScript.addScene(SceneJudge.class);
        mikuScript.setStartSceneCode(SceneStart.class);

        MikuEngine engine = new MikuEngine(mikuScript);

        return engine.swift(Map.of(
                "raw_question", "千本樱这个歌是讲什么东西的，里面有什么典型片段"
        ));
    }

}
