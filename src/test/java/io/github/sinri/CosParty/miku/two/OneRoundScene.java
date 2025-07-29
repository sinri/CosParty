package io.github.sinri.CosParty.miku.two;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.CosParty.facade.context.conversation.Actor;
import io.github.sinri.CosParty.facade.context.conversation.Conversation;
import io.github.sinri.CosParty.facade.context.conversation.ConversationContext;
import io.github.sinri.CosParty.facade.context.conversation.Speech;
import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class OneRoundScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<String> playInner(@Nonnull CosplayContext cosplayContext, @Nonnull KeelIssueRecorder<KeelEventLog> logger) {
        String conversationCode = cosplayContext.readString(DiscussionScript.FIELD_CONVERSATION_CODE);
        Integer conversationContextId = cosplayContext.readInteger(DiscussionScript.FIELD_CONVERSATION_CONTEXT_ID);
        ConversationContext conversationContext = cosplayContext.getConversationContext(conversationContextId);

        Conversation conversation = conversationContext.getConversation(conversationCode);
        List<Actor> actors = conversationContext.getActors();

        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        return Keel.asyncCallIteratively(actors, actor -> {
                       if (Objects.equals(actor.getActorName(), "主持人")) {
                           // 主持人跳过
                           return Future.succeededFuture();
                       }
                       return mixChatKit.chatStream(SupportedModelEnum.QwenPlus, req -> {
                                            req.addMessage(msg -> msg
                                                    .setRole("system")
                                                    .setTextContent("你是%s，%s\n你正在参与这场主题讨论。"
                                                            .formatted(actor.getActorName(), actor.getActorInstruction()))
                                            );

                                            Iterable<Speech> iterableOfSpeechList = conversation.getIterableOfSpeechList();
                                            StringBuilder history = new StringBuilder();
                                            history.append("至今为止的讨论发言记录如下：\n");
                                            iterableOfSpeechList.forEach(speech -> {
                                                history.append(speech.getActorName()).append("：\n\t").append(speech.getContent()).append("\n");
                                            });
                                            req.addMessage(msg -> msg
                                                    .setRole("user")
                                                    .setTextContent(
                                                            """
                                                            %s
                                                            现在论到你发言了。
                                                            请直接发表意见，每次发言仅围绕一个中心论点，简短直接。
                                                            """.formatted(
                                                                    history.toString()
                                                            ))
                                            );
                                        })
                                        .compose(response -> {
                                            String textContent = response.getMessage().getTextContent();
                                            logger.info("As " + actor.getActorName() + ": \n" + textContent);

                                            conversation.addSpeech(new Speech().setActorName(actor.getActorName())
                                                                               .setContent(textContent));
                                            return Future.succeededFuture();
                                        });
                   })
                   .compose(oneRoundOver -> {
                       Integer roundCount = cosplayContext.readInteger(DiscussionScript.FIELD_ROUND_COUNT);
                       cosplayContext.writeNumber(DiscussionScript.FIELD_ROUND_COUNT, Objects.requireNonNullElse(roundCount, 0) + 1);

                       return Future.succeededFuture(AfterOneRoundScene.class.getName());
                   });
    }
}
