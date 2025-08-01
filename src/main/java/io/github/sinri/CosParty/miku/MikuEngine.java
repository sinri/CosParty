package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayEngine;
import io.github.sinri.CosParty.kernel.CosplayScript;
import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public final class MikuEngine extends CosplayEngine {

    public MikuEngine(@Nonnull CosplayScript cosplayScript) {
        super(cosplayScript);
    }

    @Override
    protected Future<Void> initialize() {
        this.contextOnScriptScope = CosplayContext.withMemory();
        return Future.succeededFuture();
    }

}
