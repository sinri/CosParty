package io.github.sinri.CosParty.miku;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.CosParty.facade.CosplayEngine;
import io.github.sinri.CosParty.facade.CosplayScript;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.UUID;

public class MikuEngine extends CosplayEngine {
    private final String engineId;

    public MikuEngine(@Nonnull CosplayScript cosplayScript, @Nonnull MixChatKit mixChatKit) {
        super(cosplayScript);
        this.engineId = getClass().getName() + "@" + UUID.randomUUID();
        this.mixChatKit = mixChatKit;
    }

    @Override
    protected Future<Void> initialize() {
        this.cosplayContext = CosplayContext.withMemory(engineId);
        return Future.succeededFuture();
    }

    public String getEngineId() {
        return engineId;
    }


}
