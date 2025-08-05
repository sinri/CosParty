package io.github.sinri.CosParty.miku.three;

import io.github.sinri.CosParty.miku.MikuEngine;
import io.github.sinri.drydock.naval.raider.Privateer;
import io.vertx.core.Future;

public class Debate extends Privateer {
    @Override
    protected Future<Void> launchAsPrivateer() {
        DebateScript debateScript = new DebateScript();
        MikuEngine mikuEngine = new MikuEngine(debateScript);
        return mikuEngine.startup(ctx->{
            ctx.writeString("subject","中国在遭受印度武装侵略时，要不要征服印度");
        });
    }
}
