package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableDataUnit;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 对话中的发言记录。
 * <p>
 * 由特定角色发表的一段内容。
 */
public class Speech implements JsonifiableDataUnit {
    /**
     * 发言角色的名称。
     */
    private String actorName;

    /**
     * 角色发表的内容。
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
     * @return 当前 Speech 实例
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
     * @return 当前 Speech 实例
     */
    public Speech setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 将当前发言记录转换为 JSON 对象。
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
     * @param jsonObject 包含发言信息的JSON对象
     */
    @Override
    public void reloadData(@Nonnull JsonObject jsonObject) {
        this.setActorName(jsonObject.getString("actor_name"))
            .setContent(jsonObject.getString("content"));
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
