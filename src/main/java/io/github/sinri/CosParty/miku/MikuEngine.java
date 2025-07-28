package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.facade.CosplayEngine;
import io.github.sinri.CosParty.facade.CosplayScript;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class MikuEngine extends CosplayEngine {

    public MikuEngine(@Nonnull CosplayScript cosplayScript) {
        super(cosplayScript);
    }

    @Override
    protected Future<Void> initialize() {
        this.cosplayContext = CosplayContext.withMemory(getEngineId());
        return Future.succeededFuture();
    }

}
