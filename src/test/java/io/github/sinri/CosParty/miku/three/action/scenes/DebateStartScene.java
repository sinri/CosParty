package io.github.sinri.CosParty.miku.three.action.scenes;

import io.github.sinri.CosParty.kernel.context.conversation.Conversation;
import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;
import io.github.sinri.CosParty.kernel.context.conversation.Speech;
import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.github.sinri.CosParty.miku.three.action.ModeratorActor;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class DebateStartScene extends MikuScene {
    @Nonnull
    @Override
    public Future<Void> play() {
        String debateTopic = context().readString(DebateAction.FIELD_DEBATE_MOTION);
        getLogger().info("[DebateStartScene] debateTopic: " + debateTopic);

        ConversationContext conversationContext = context().getConversationContext(0);

        Conversation conversation = new Conversation();
        conversationContext.registerConversation(conversation);

        context().writeString(DebateAction.FIELD_CONVERSATION_CODE, conversation.getConversationCode());

        conversation.addSpeech(new Speech()
                .setActorName(ModeratorActor.ACTOR_NAME)
                .setContent("""
                            本次辩论的议题是“%s”；采用四轮制辩论。
                            请双方依次发言，每次发言简明扼要。
                            """
                        .formatted(debateTopic)
                )
        );

        return Future.succeededFuture();
    }
}
