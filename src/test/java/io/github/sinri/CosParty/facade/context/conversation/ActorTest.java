package io.github.sinri.CosParty.facade.context.conversation;

import io.github.sinri.keel.facade.tesuto.unit.KeelUnitTest;
import io.vertx.core.json.JsonArray;
import org.junit.jupiter.api.Test;

class ActorTest extends KeelUnitTest {
    @Test
    void jsonifyIt() {
        Actor actor = new Actor()
                .setActorName("a")
                .setActorInstruction("b");
        getUnitTestLogger().info("actor: " + actor);

        JsonArray array = new JsonArray()
                .add(actor);
        getUnitTestLogger().info("[actor]: " + array);
    }
}