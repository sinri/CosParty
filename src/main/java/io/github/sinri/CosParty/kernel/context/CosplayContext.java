package io.github.sinri.CosParty.kernel.context;

import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * 角色扮演场景的状态和数据管理接口。
 * <p>
 * 提供键值对存储机制，支持字符串、数值、JSON对象等多种数据类型。
 * 所有数据读写操作均为同步接口，持久化需要自行实现。
 * <p>
 * 实现类应确保线程安全，特别是在多线程环境下使用内存存储时。
 *
 * @since 1.0
 */
public interface CosplayContext {
    /**
     * 创建基于内存存储的角色扮演上下文实例。
     * <p>
     * 适用于临时场景，数据仅在内存中保存，场景结束后自动清理。
     *
     * @return 新的内存角色扮演上下文实例
     */
    static CosplayContext withMemory() {
        return new CosplayContextOnMemory();
    }

    /**
     * 获取上下文的唯一标识符。
     *
     * @return 上下文ID，非空字符串
     */
    @Nonnull
    String getContextId();

    /**
     * 读取字符串值。
     * <p>
     * 如果键不存在或值为null，返回null。
     *
     * @param key 数据键
     * @return 对应的字符串值，可能为null
     */
    @Nullable
    String readString(@Nonnull String key);

    /**
     * 读取长整型值。
     * <p>
     * 将字符串值转换为Long类型，转换失败时返回null。
     *
     * @param key 数据键
     * @return 长整型值，转换失败时返回null
     */
    @Nullable
    default Long readLong(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return Long.parseLong(s);
    }

    /**
     * 读取整型值。
     * <p>
     * 将字符串值转换为Integer类型，转换失败时返回null。
     *
     * @param key 数据键
     * @return 整型值，转换失败时返回null
     */
    @Nullable
    default Integer readInteger(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return Integer.parseInt(s);
    }

    /**
     * 读取双精度浮点值。
     * <p>
     * 将字符串值转换为Double类型，转换失败时返回null。
     *
     * @param key 数据键
     * @return 双精度浮点值，转换失败时返回null
     */
    @Nullable
    default Double readDouble(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return Double.parseDouble(s);
    }

    /**
     * 读取BigDecimal值。
     * <p>
     * 将字符串值转换为BigDecimal类型，适用于精确的十进制运算。
     *
     * @param key 数据键
     * @return BigDecimal值，转换失败时返回null
     */
    @Nullable
    default BigDecimal readBigDecimal(@Nonnull String key) {
        var s = readString(key);
        if (s == null) {
            return null;
        }
        return new BigDecimal(s);
    }

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
     * 写入字符串值。
     * <p>
     * 将字符串值与指定键关联存储。
     *
     * @param key   数据键
     * @param value 字符串值
     * @return 当前上下文实例，支持链式调用
     */
    CosplayContext writeString(@Nonnull String key, @Nonnull String value);

    /**
     * 写入数值。
     * <p>
     * 将数值转换为字符串后存储。
     *
     * @param key   数据键
     * @param value 数值
     * @return 当前上下文实例，支持链式调用
     */
    default CosplayContext writeNumber(@Nonnull String key, @Nonnull Number value) {
        return writeString(key, value.toString());
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
    default CosplayContext writeJsonObject(@Nonnull String key, @Nonnull JsonObject jsonObject) {
        return writeString(key, jsonObject.toString());
    }

    /**
     * 获取指定ID的对话上下文。
     * <p>
     * 如果ID不存在，具体行为由实现类决定。
     *
     * @param conversationContextId 对话上下文ID
     * @return 对应的对话上下文
     */
    ConversationContext getConversationContext(int conversationContextId);

    /**
     * 创建新的对话上下文。
     * <p>
     * 生成具有唯一ID的对话上下文实例。
     *
     * @return 新创建的对话上下文
     */
    ConversationContext createConversationContext();

    /**
     * 从另一个上下文复制数据到当前上下文。
     * <p>
     * 如果源数据不存在，则不执行任何操作。
     *
     * @param anotherContext            源上下文
     * @param fieldNameInAnotherContext 源上下文中的字段名
     * @param fieldNameInThisContext    当前上下文中的字段名
     * @return 当前上下文实例，支持链式调用
     */
    default CosplayContext pullFromAnotherContext(
            @Nonnull CosplayContext anotherContext,
            @Nonnull String fieldNameInAnotherContext,
            @Nonnull String fieldNameInThisContext
    ) {
        String s = anotherContext.readString(fieldNameInAnotherContext);
        if (s == null) return this;
        return this.writeString(fieldNameInThisContext, s);
    }
}
