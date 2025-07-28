package io.github.sinri.CosParty.facade.context;

import io.vertx.core.Future;
import io.vertx.core.shareddata.LocalMap;

import javax.annotation.Nonnull;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

class CosplayContextOnMemory implements CosplayContext {
    private final LocalMap<String, String> contextMap;

    CosplayContextOnMemory(String engineId) {
        contextMap = Keel.getVertx().sharedData().getLocalMap(engineId);
    }

    @Override
    public Future<String> readString(@Nonnull String key) {
        String s = contextMap.get(key);
        return Future.succeededFuture(s);
    }

    @Override
    public Future<Void> writeString(@Nonnull String key, @Nonnull String value) {
        contextMap.put(key, value);
        return Future.succeededFuture();
    }

}
