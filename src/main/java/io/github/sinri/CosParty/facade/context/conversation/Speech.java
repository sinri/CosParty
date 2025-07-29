package io.github.sinri.CosParty.facade.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableEntity;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 在一个{@link Conversation}内的一个发言记录，即由特定的角色发表一段内容。
 */
public class Speech implements JsonifiableEntity<Speech> {
    /**
     * 通过本字段记录发言的角色的名称。
     *
     * @see Actor#getActorName()
     */
    private String actorName;
    /**
     * 指定角色发表的内容。
     */
    private String content;

    public String getActorName() {
        return actorName;
    }

    public Speech setActorName(String actorName) {
        this.actorName = actorName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Speech setContent(String content) {
        this.content = content;
        return this;
    }

    @Nonnull
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("actor_name", actorName)
                .put("content", content)
                ;
    }

    @Nonnull
    @Override
    public Speech reloadDataFromJsonObject(@Nonnull JsonObject jsonObject) {
        return new Speech()
                .setActorName(jsonObject.getString("actor_name"))
                .setContent(jsonObject.getString("content"))
                ;
    }

    @Nonnull
    @Override
    public Speech getImplementation() {
        return this;
    }

    @Override
    public String toString() {
        return toJsonExpression();
    }
}
