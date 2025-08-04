package io.github.sinri.CosParty.kernel.context;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CosplayContextJsonMixin extends CosplayContextCore {
    /**
     * 读取JSON对象。
     * <p>
     * 将字符串值解析为JsonObject，JSON格式无效时返回null。
     *
     * @param key 数据键
     * @return JSON对象，解析失败时返回null
     */
    @Nullable
    default JsonObject readJsonObject(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return new JsonObject(s);
    }

    /**
     * 读取JSON数组。
     * <p>
     * 将字符串值解析为JsonArray，JSON格式无效时返回null。
     *
     * @param key 数据键
     * @return JSON数组，解析失败时返回null
     */
    @Nullable
    default JsonArray readJsonArray(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return new JsonArray(s);
    }


    /**
     * 写入JSON对象。
     * <p>
     * 将JsonObject转换为字符串后存储。
     *
     * @param key        数据键
     * @param jsonObject JSON对象
     * @return 当前上下文实例，支持链式调用
     */
    default void writeJsonObject(@Nonnull String key, @Nonnull JsonObject jsonObject) {
        writeString(key, jsonObject.toString());
    }
}
