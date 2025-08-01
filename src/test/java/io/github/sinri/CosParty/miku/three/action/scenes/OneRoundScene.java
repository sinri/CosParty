package io.github.sinri.CosParty.miku.three.action.scenes;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.kernel.context.conversation.Actor;
import io.github.sinri.CosParty.kernel.context.conversation.Conversation;
import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;
import io.github.sinri.CosParty.kernel.context.conversation.Speech;
import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.AffirmativeActor;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.github.sinri.CosParty.miku.three.action.NegativeActor;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.Objects;

public class OneRoundScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<Void> playInner() {
        String conversationCode = getCurrentContext().readString(DebateAction.FIELD_CONVERSATION_CODE);

        ConversationContext conversationContext = getCurrentContext().getConversationContext(0);
        Conversation conversation = conversationContext.getConversation(conversationCode);

        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        Actor affirmativeActor = conversationContext.getActor(AffirmativeActor.ACTOR_NAME);
        Actor negativeActor = conversationContext.getActor(NegativeActor.ACTOR_NAME);

        return actorSay(mixChatKit, affirmativeActor, conversation)
                .compose(v -> {
                    return actorSay(mixChatKit, negativeActor, conversation);
                })
                .compose(v -> {
                    Integer roundCount = getCurrentContext().readInteger(DebateAction.FIELD_ROUND_COUNT);
                    getCurrentContext().writeNumber(DebateAction.FIELD_ROUND_COUNT, Objects.requireNonNullElse(roundCount, 0) + 1);

                    return Future.succeededFuture();
                });
    }

    private Future<Void> actorSay(MixChatKit mixChatKit, Actor actor, Conversation conversation) {
        StringBuilder sb = new StringBuilder();
        conversation.getIterableOfSpeechList().forEach(speech -> {
            sb.append(speech.getActorName()).append("：\n\t").append(speech.getContent()).append("\n");
        });

        return mixChatKit.chatStream(SupportedModelEnum.QwenPlus, req -> {
                             req.addMessage(msg -> msg
                                     .setRole("system")
                                     .setTextContent(
                                             """
                                             你正在参加辩论，%s
                                             请按照辩论发言记录进行本轮发言。
                                             """
                                                     .formatted(actor.getActorInstruction()))
                             );
                             req.addMessage(msg -> msg
                                     .setRole("user")
                                     .setTextContent(
                                             """
                                             %s
                                             
                                             下面请%s发言，直接输出发言内容。
                                             """
                                                     .formatted(sb.toString(), actor.getActorName())
                                     )
                             );
                         })
                         .compose(response -> {
                             String textContent = response.getMessage().getTextContent();

                             getLogger().info(actor.getActorName() + ":\n\t" + textContent);

                             conversation.addSpeech(new Speech().setActorName(actor.getActorName())
                                                                .setContent(textContent));
                             return Future.succeededFuture();
                         });
    }
}
