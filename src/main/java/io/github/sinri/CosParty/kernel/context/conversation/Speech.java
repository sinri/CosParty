package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableEntity;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 在一个{@link Conversation}内的一个发言记录，即由特定的角色发表一段内容。
 * <p>
 * 该类实现了 {@link JsonifiableEntity} 接口，支持 JSON 序列化和反序列化操作。
 *
 * @see Conversation
 * @see Actor
 * @since 1.0
 */
public class Speech implements JsonifiableEntity<Speech> {
    /**
     * 发言角色的名称，与 {@link Actor#getActorName()} 返回值保持一致。
     * 在序列化时使用 "actor_name" 作为 JSON 键名。
     */
    private String actorName;

    /**
     * 角色发表的内容。在序列化时使用 "content" 作为 JSON 键名。
     */
    private String content;

    /**
     * 获取发言角色的名称。
     *
     * @return 发言角色的名称
     */
    public String getActorName() {
        return actorName;
    }

    /**
     * 设置发言角色的名称。
     *
     * @param actorName 发言角色的名称
     * @return 当前 Speech 实例，支持链式调用
     */
    public Speech setActorName(String actorName) {
        this.actorName = actorName;
        return this;
    }

    /**
     * 获取发言内容。
     *
     * @return 发言内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置发言内容。
     *
     * @param content 角色发表的文本内容
     * @return 当前 Speech 实例，支持链式调用
     */
    public Speech setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 将当前发言记录转换为 JSON 对象。
     * <p>
     * JSON 结构：{"actor_name": "角色名称", "content": "发言内容"}
     *
     * @return 包含发言记录数据的 JSON 对象
     */
    @Nonnull
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("actor_name", actorName)
                .put("content", content)
                ;
    }

    /**
     * 从 JSON 对象重新加载发言记录数据。
     *
     * @param jsonObject 参照{@link Speech#toJsonObject()}的返回结果
     */
    @Nonnull
    @Override
    public Speech reloadDataFromJsonObject(@Nonnull JsonObject jsonObject) {
        return this.setActorName(jsonObject.getString("actor_name"))
                   .setContent(jsonObject.getString("content"));
    }

    /**
     * 获取当前实现类的实例。
     *
     * @return 当前 Speech 实例
     */
    @Nonnull
    @Override
    public Speech getImplementation() {
        return this;
    }

    /**
     * 返回发言记录的 JSON 字符串表示。
     *
     * @return 发言记录的 JSON 字符串表示
     */
    @Override
    public String toString() {
        return toJsonExpression();
    }
}
