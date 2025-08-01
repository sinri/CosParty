package io.github.sinri.CosParty.miku.three.action.scenes;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.kernel.context.conversation.Actor;
import io.github.sinri.CosParty.kernel.context.conversation.Conversation;
import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;
import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.github.sinri.CosParty.miku.three.action.ModeratorActor;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class DebateEndScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<Void> playInner() {
        String conversationCode = getCurrentContext().readString(DebateAction.FIELD_CONVERSATION_CODE);

        ConversationContext conversationContext = getCurrentContext().getConversationContext(0);
        Conversation conversation = conversationContext.getConversation(conversationCode);

        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        StringBuilder sb = new StringBuilder();
        conversation.getIterableOfSpeechList().forEach(speech -> {
            sb.append(speech.getActorName()).append("：\n\t").append(speech.getContent()).append("\n");
        });

        Actor moderatorActor = conversationContext.getActor(ModeratorActor.ACTOR_NAME);

        return mixChatKit.chatStream(SupportedModelEnum.QwenPlus, req -> {
                             req.addMessage(msg -> msg
                                     .setRole("system")
                                     .setTextContent(
                                             """
                                             你是这场辩论的主持人。
                                             %s
                                             """
                                                     .formatted(moderatorActor.getActorInstruction()))
                             );
                             req.addMessage(msg -> msg
                                     .setRole("user")
                                     .setTextContent(
                                             """
                                             %s
                                             
                                             下面请%s作总结发言，直接输出发言内容。
                                             """
                                                     .formatted(sb.toString(), moderatorActor.getActorName())
                                     )
                             );
                         })
                         .compose(response -> {
                             String textContent = response.getMessage().getTextContent();

                             getLogger().info(moderatorActor.getActorName() + ":\n\t" + textContent);

                             // conversation.addSpeech(new Speech().setActorName(moderatorActor.getActorName()).setContent(textContent));

                             getCurrentContext().writeString(DebateAction.FIELD_DEBATE_CONCLUSION, textContent);
                             return Future.succeededFuture();
                         });
    }
}
