package io.github.sinri.CosParty.facade.context;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public interface CosplayContext {
    static CosplayContext withMemory(String engineId) {
        return new CosplayContextOnMemory(engineId);
    }

    /**
     * @param key the key to fetch value from context.
     * @return the value fetched mapped with the provided {@code key}, which might be {@code null}.
     */
    Future<String> readString(@Nonnull String key);

    default Future<Long> readLong(@Nonnull String key) {
        return readString(key)
                .compose(s -> {
                    if (s == null) {
                        return Future.succeededFuture(null);
                    }
                    var x = Long.parseLong(s);
                    return Future.succeededFuture(x);
                });
    }

    default Future<Integer> readInteger(@Nonnull String key) {
        return readString(key)
                .compose(s -> {
                    if (s == null) {
                        return Future.succeededFuture(null);
                    }
                    var x = Integer.parseInt(s);
                    return Future.succeededFuture(x);
                });
    }

    default Future<Double> readDouble(@Nonnull String key) {
        return readString(key)
                .compose(s -> {
                    if (s == null) {
                        return Future.succeededFuture(null);
                    }
                    var x = Double.parseDouble(s);
                    return Future.succeededFuture(x);
                });
    }

    default Future<BigDecimal> readBigDecimal(@Nonnull String key) {
        return readString(key)
                .compose(s -> {
                    if (s == null) {
                        return Future.succeededFuture(null);
                    }
                    var x = new BigDecimal(s);
                    return Future.succeededFuture(x);
                });
    }

    default Future<JsonObject> readJsonObject(@Nonnull String key) {
        return readString(key)
                .compose(s -> {
                    if (s == null) {
                        return Future.succeededFuture(null);
                    }
                    var x = new JsonObject(s);
                    return Future.succeededFuture(x);
                });
    }

    Future<Void> writeString(@Nonnull String key, @Nonnull String value);

    default Future<Void> writeNumber(@Nonnull String key, @Nonnull Number value) {
        return writeString(key, value.toString());
    }

    default Future<Void> writeJsonObject(@Nonnull String key, @Nonnull JsonObject jsonObject) {
        return writeString(key, jsonObject.toString());
    }
}
