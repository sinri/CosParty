package io.github.sinri.CosParty.miku.two;

import io.github.sinri.CosParty.kernel.context.conversation.Actor;
import io.github.sinri.CosParty.kernel.context.conversation.Conversation;
import io.github.sinri.CosParty.kernel.context.conversation.Speech;
import io.github.sinri.CosParty.miku.MikuScene;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;

public class StartScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<Void> playInner() {
        String topic = getCurrentContext().readString(DiscussionScript.FIELD_TOPIC);

        var conversationContext = getCurrentContext().createConversationContext();
        getCurrentContext().writeNumber(DiscussionScript.FIELD_CONVERSATION_CONTEXT_ID, conversationContext.getConversationContextIndex());

        Actor actorHost = new Actor()
                .setActorName("主持人")
                .setActorInstruction("围绕议题主持讨论，在各方充分发表意见后进行分析总结，并给出自己的结论。");
        conversationContext.registerActor(actorHost);
        var array = getCurrentContext().readJsonArray(DiscussionScript.FIELD_MEMBERS);
        array.forEach(item -> {
            var actor = new Actor().reloadDataFromJsonObject((JsonObject) item);
            conversationContext.registerActor(actor);
        });

        Conversation conversation = new Conversation();
        conversation.addSpeech(new Speech()
                .setActorName("主持人")
                .setContent("今天我们讨论的话题是%s，大家可以轮流发表意见，畅欲所言。".formatted(topic))
        );
        conversationContext.registerConversation(conversation);
        getCurrentContext().writeString(DiscussionScript.FIELD_CONVERSATION_CODE, conversation.getConversationCode());

        return Future.succeededFuture();
    }
}
