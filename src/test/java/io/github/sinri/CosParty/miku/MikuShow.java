package io.github.sinri.CosParty.miku;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.drydock.naval.raider.Privateer;
import io.vertx.core.Future;

public class MikuShow extends Privateer {
    private final static String startingSceneCode = "STARTING_SCENE";
    private final static String judgeSceneCode = "JUDGE_SCENE";

    @Override
    protected Future<Void> launchAsPrivateer() {
        // AigcMix.enableVerboseLogger();

        SceneStart sceneStart = new SceneStart("千本樱这个歌是讲什么东西的，里面有什么典型片段");
        SceneJudge sceneJudge = new SceneJudge();

        MikuScript mikuScript = new MikuScript(sceneStart);
        mikuScript.addScene(sceneJudge);

        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        MikuEngine engine = new MikuEngine(mikuScript, mixChatKit);

        return engine.swift();
    }

}
