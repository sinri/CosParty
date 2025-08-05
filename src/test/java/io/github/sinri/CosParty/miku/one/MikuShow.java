package io.github.sinri.CosParty.miku.one;

import io.github.sinri.CosParty.miku.MikuEngine;
import io.github.sinri.drydock.naval.raider.Privateer;
import io.vertx.core.Future;

public class MikuShow extends Privateer {

    @Override
    protected Future<Void> launchAsPrivateer() {
        // AigcMix.enableVerboseLogger();

        SampleScript mikuScript = new SampleScript();
        MikuEngine engine = new MikuEngine(mikuScript);

        return engine.startup(ctx -> {
            ctx.writeString("raw_question", "千本樱这个歌是讲什么东西的，里面有什么典型片段");
        });
    }

}
