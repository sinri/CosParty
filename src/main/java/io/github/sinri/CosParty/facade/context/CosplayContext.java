package io.github.sinri.CosParty.facade.context;

import io.github.sinri.CosParty.facade.context.conversation.ConversationContext;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

public interface CosplayContext {
    static CosplayContext withMemory(String engineId) {
        return new CosplayContextOnMemory(engineId);
    }

    /**
     * @param key the key to fetch value from context.
     * @return the value fetched mapped with the provided {@code key}, which might be {@code null}.
     */
    @Nullable
    String readString(@Nonnull String key);

    @Nullable
    default Long readLong(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return Long.parseLong(s);
    }

    @Nullable
    default Integer readInteger(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return Integer.parseInt(s);
    }

    @Nullable
    default Double readDouble(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return Double.parseDouble(s);
    }

    @Nullable
    default BigDecimal readBigDecimal(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return new BigDecimal(s);
    }

    @Nullable
    default JsonObject readJsonObject(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return new JsonObject(s);
    }

    @Nullable
    default JsonArray readJsonArray(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return new JsonArray(s);
    }

    CosplayContext writeString(@Nonnull String key, @Nonnull String value);

    default CosplayContext writeNumber(@Nonnull String key, @Nonnull Number value) {
        return writeString(key, value.toString());
    }

    default CosplayContext writeJsonObject(@Nonnull String key, @Nonnull JsonObject jsonObject) {
        return writeString(key, jsonObject.toString());
    }

    ConversationContext getConversationContext(int conversationContextId);

    ConversationContext createConversationContext();
}
