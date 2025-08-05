package io.github.sinri.CosParty.miku.two;

import io.github.sinri.CosParty.kernel.context.conversation.Conversation;
import io.github.sinri.CosParty.kernel.context.conversation.DynamicActor;
import io.github.sinri.CosParty.kernel.context.conversation.Speech;
import io.github.sinri.CosParty.miku.MikuScene;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

public class StartScene extends MikuScene implements DiscussionContextMixin {
    @Nonnull
    @Override
    public Future<Void> play() {
        String topic = topic();

        var conversationContext = context().createConversationContext();
        conversationContextId(conversationContext.getConversationContextIndex());

        DynamicActor actorHost = new DynamicActor()
                .setActorName("主持人")
                .setActorInstruction("围绕议题主持讨论，在各方充分发表意见后进行分析总结，并给出自己的结论。");
        conversationContext.registerActor(actorHost);
        var array = members();
        Objects.requireNonNull(array);
        array.forEach(item -> {
            var actor = new DynamicActor();
            actor.reloadData((JsonObject) item);
            conversationContext.registerActor(actor);
        });

        Conversation conversation = new Conversation();
        conversation.addSpeech(new Speech()
                .setActorName("主持人")
                .setContent("今天我们讨论的话题是%s，大家可以轮流发表意见，畅欲所言。".formatted(topic))
        );
        conversationContext.registerConversation(conversation);
        conversationCode(conversation.getConversationCode());

        return Future.succeededFuture();
    }
}
