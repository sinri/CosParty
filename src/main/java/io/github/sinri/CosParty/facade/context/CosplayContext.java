package io.github.sinri.CosParty.facade.context;

import io.github.sinri.CosParty.facade.context.conversation.ConversationContext;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * 用于管理角色扮演场景中状态和数据的上下文接口。
 * <p>
 * 此接口提供了读写各种数据类型的方法，这些数据可以持久化存储，
 * 并在角色扮演场景的不同场景和对话之间共享。
 * <p>
 * 上下文支持基本类型、复杂对象，以及通过专用对话上下文进行对话管理。
 */
public interface CosplayContext {
    /**
     * 创建一个新的内存角色扮演上下文实例。
     * <p>
     * 此工厂方法提供了一种便捷的方式来创建在场景持续期间
     * 将所有数据存储在内存中的上下文。
     *
     * @param engineId 角色扮演引擎的唯一标识符
     * @return 一个新的内存角色扮演上下文实例
     */
    static CosplayContext withMemory(String engineId) {
        return new CosplayContextOnMemory(engineId);
    }

    /**
     * 从上下文中读取字符串值。
     * <p>
     * 获取与指定键关联的值。如果键不存在或值为null，此方法返回null。
     *
     * @param key 用于从上下文获取值的键
     * @return 与提供的{@code key}映射的获取值，可能为{@code null}
     */
    @Nullable
    String readString(@Nonnull String key);

    /**
     * 从上下文中读取长整型值。
     * <p>
     * 将与键关联的字符串值转换为Long类型。如果键不存在或值无法解析为long，
     * 则返回null。
     *
     * @param key 用于从上下文获取值的键
     * @return 解析后的Long值，如果未找到或无效则返回null
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
     * 从上下文中读取整型值。
     * <p>
     * 将与键关联的字符串值转换为Integer类型。如果键不存在或值无法解析为整数，
     * 则返回null。
     *
     * @param key 用于从上下文获取值的键
     * @return 解析后的Integer值，如果未找到或无效则返回null
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
     * 从上下文中读取双精度浮点值。
     * <p>
     * 将与键关联的字符串值转换为Double类型。如果键不存在或值无法解析为double，
     * 则返回null。
     *
     * @param key 用于从上下文获取值的键
     * @return 解析后的Double值，如果未找到或无效则返回null
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
     * 从上下文中读取BigDecimal值。
     * <p>
     * 将与键关联的字符串值转换为BigDecimal类型。如果键不存在或值无法解析为BigDecimal，
     * 则返回null。此方法适用于精确的十进制算术运算。
     *
     * @param key 用于从上下文获取值的键
     * @return 解析后的BigDecimal值，如果未找到或无效则返回null
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
     * 从上下文中读取JsonObject。
     * <p>
     * 将与键关联的字符串值解析为JSON对象。如果键不存在或值不是有效的JSON，
     * 则返回null。
     *
     * @param key 用于从上下文获取值的键
     * @return 解析后的JsonObject，如果未找到或JSON无效则返回null
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
     * 从上下文中读取JsonArray。
     * <p>
     * 将与键关联的字符串值解析为JSON数组。如果键不存在或值不是有效的JSON，
     * 则返回null。
     *
     * @param key 用于从上下文获取值的键
     * @return 解析后的JsonArray，如果未找到或JSON无效则返回null
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
     * 向上下文写入字符串值。
     * <p>
     * 将提供的值与指定键一起存储在上下文中。该值将在角色扮演场景的持续期间保持。
     *
     * @param key 与值关联的键
     * @param value 要存储的字符串值
     * @return 此上下文实例，用于方法链式调用
     */
    CosplayContext writeString(@Nonnull String key, @Nonnull String value);

    /**
     * 向上下文写入数值。
     * <p>
     * 将数字转换为字符串并使用指定键存储。此方法提供了存储任何数字类型的便捷方式。
     *
     * @param key 与值关联的键
     * @param value 要存储的数值
     * @return 此上下文实例，用于方法链式调用
     */
    default CosplayContext writeNumber(@Nonnull String key, @Nonnull Number value) {
        return writeString(key, value.toString());
    }

    /**
     * 向上下文写入JsonObject。
     * <p>
     * 将JsonObject转换为字符串表示形式并使用指定键存储，以便后续检索。
     *
     * @param key 与值关联的键
     * @param jsonObject 要存储的JSON对象
     * @return 此上下文实例，用于方法链式调用
     */
    default CosplayContext writeJsonObject(@Nonnull String key, @Nonnull JsonObject jsonObject) {
        return writeString(key, jsonObject.toString());
    }

    /**
     * 通过ID获取现有的对话上下文。
     * <p>
     * 返回与指定ID关联的对话上下文。如果不存在具有该ID的对话上下文，
     * 行为取决于具体实现。
     *
     * @param conversationContextId 对话上下文的唯一标识符
     * @return 具有指定ID的对话上下文
     */
    ConversationContext getConversationContext(int conversationContextId);

    /**
     * 创建新的对话上下文。
     * <p>
     * 生成具有唯一标识符的新对话上下文，并返回它以用于管理对话状态和历史记录。
     *
     * @return 新创建的对话上下文
     */
    ConversationContext createConversationContext();
}
