package io.github.sinri.CosParty.facade.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableEntity;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

/**
 * 在一个{@link ConversationContext}下通用的在AI大模型下运作的角色。
 */
public class Actor implements JsonifiableEntity<Actor> {
    private String actorName;
    private String actorInstruction;

    public String getActorName() {
        return this.actorName;
    }

    public Actor setActorName(String actorName) {
        this.actorName = actorName;
        return this;
    }

    public String getActorInstruction() {
        return this.actorInstruction;
    }

    public Actor setActorInstruction(String actorInstruction) {
        this.actorInstruction = actorInstruction;
        return this;
    }


    @Nonnull
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("actor_name", actorName)
                .put("actor_instruction", actorInstruction)
                ;
    }

    @Nonnull
    @Override
    public Actor reloadDataFromJsonObject(@Nonnull JsonObject jsonObject) {
        return new Actor()
                .setActorName(jsonObject.getString("actor_name"))
                .setActorInstruction(jsonObject.getString("actor_instruction"))
                ;
    }

    @Nonnull
    @Override
    public Actor getImplementation() {
        return this;
    }

    @Override
    public String toString() {
        return toJsonExpression();
    }
}
