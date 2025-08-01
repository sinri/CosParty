package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableDataUnit;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

public interface Actor extends JsonifiableDataUnit {
    @Nonnull
    String getActorName();

    @Nonnull
    String getActorInstruction();

    @Nonnull
    default JsonObject toJsonObject() {
        return new JsonObject()
                .put("actor_name", getActorName())
                .put("actor_instruction", getActorInstruction())
                ;
    }
}
