package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableEntity;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 在一个{@link ConversationContext}下进行的一场连续的对话。
 * <p>
 * 对话包含多个发言（{@link Speech}），每个发言都有特定的发言者（{@link Actor}）。
 * 对话具有唯一的对话代码，用于标识和管理。
 * <p>
 * 该类实现了{@link JsonifiableEntity}接口，支持JSON序列化和反序列化，
 * 便于数据的持久化存储和网络传输。
 * <p>
 * 线程安全：该类不是线程安全的，在多线程环境下使用时需要外部同步。
 *
 * @see ConversationContext 对话上下文
 * @since 1.0
 */
public class Conversation implements JsonifiableEntity<Conversation> {
    /**
     * 对话中的发言列表，按时间顺序存储
     */
    @Nonnull
    private final List<Speech> speechList;

    /**
     * 对话的唯一标识代码，用于区分不同的对话
     */
    private String conversationCode;

    /**
     * 创建一个新的对话实例
     * <p>
     * 初始化时会自动生成一个唯一的对话代码
     */
    public Conversation() {
        this.speechList = new ArrayList<>();
        this.conversationCode = UUID.randomUUID().toString();
    }

    /**
     * 获取对话的唯一标识代码
     *
     * @return 对话代码字符串
     */
    public String getConversationCode() {
        return conversationCode;
    }

    /**
     * 向对话中添加一个新的发言
     * <p>
     * 发言会被添加到发言列表的末尾，保持时间顺序
     *
     * @param speech 要添加的发言，不能为null
     * @return 当前对话实例，支持链式调用
     * @throws IllegalArgumentException 如果speech为null
     */
    public Conversation addSpeech(@Nonnull Speech speech) {
        this.speechList.add(speech);
        return this;
    }

    /**
     * 获取对话中所有发言的可迭代对象
     * <p>
     * 返回的迭代器按照发言的时间顺序进行遍历
     *
     * @return 发言列表的可迭代对象
     */
    public Iterable<Speech> getIterableOfSpeechList() {
        return this.speechList;
    }

    /**
     * 将对话对象序列化为JSON格式
     * <p>
     * 包含对话代码和所有发言的JSON表示
     *
     * @return 包含对话数据的JSON对象
     */
    @Nonnull
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("conversation_code", conversationCode)
                .put("speech_list", new JsonArray(speechList))
                ;
    }

    /**
     * 从JSON对象中重新加载对话数据
     *
     * @param jsonObject 参照{@link Conversation#toJsonObject()}的返回结果
     */
    @Nonnull
    @Override
    public Conversation reloadDataFromJsonObject(@Nonnull JsonObject jsonObject) {
        this.conversationCode = (jsonObject.getString("conversation_code"));
        this.speechList.clear();
        JsonArray array = jsonObject.getJsonArray("speech_list");
        array.forEach(item -> {
            var o = ((JsonObject) item);
            Speech speech = new Speech().reloadDataFromJsonObject(o);
            this.addSpeech(speech);
        });
        return this;
    }

    /**
     * 获取当前对话实例的实现对象
     *
     * @return 当前对话实例
     */
    @Nonnull
    @Override
    public Conversation getImplementation() {
        return this;
    }

    /**
     * 返回对话的字符串表示
     * <p>
     * 使用JSON格式表示对话内容
     *
     * @return 对话的JSON字符串表示
     */
    @Override
    public String toString() {
        return toJsonExpression();
    }
}
