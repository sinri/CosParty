package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.facade.tesuto.unit.KeelUnitTest;
import io.vertx.core.json.JsonArray;
import org.junit.jupiter.api.Test;

class DynamicActorTest extends KeelUnitTest {
    @Test
    void jsonifyIt() {
        DynamicActor actor = new DynamicActor()
                .setActorName("a")
                .setActorInstruction("b");
        getUnitTestLogger().info("actor: " + actor);

        JsonArray array = new JsonArray()
                .add(actor);
        getUnitTestLogger().info("[actor]: " + array);
    }
}