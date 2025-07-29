package io.github.sinri.CosParty.miku.two;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.CosParty.facade.context.conversation.Conversation;
import io.github.sinri.CosParty.facade.context.conversation.ConversationContext;
import io.github.sinri.CosParty.facade.context.conversation.Speech;
import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AfterOneRoundScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<String> playInner(@Nonnull CosplayContext cosplayContext, @Nonnull KeelIssueRecorder<KeelEventLog> logger) {
        Integer roundCount = cosplayContext.readInteger(DiscussionScript.FIELD_ROUND_COUNT);
        if (Objects.requireNonNullElse(roundCount, 0) > 3) {
            return Future.succeededFuture(EndScene.class.getName());
        }

        String conversationCode = cosplayContext.readString(DiscussionScript.FIELD_CONVERSATION_CODE);
        Integer conversationContextId = cosplayContext.readInteger(DiscussionScript.FIELD_CONVERSATION_CONTEXT_ID);
        ConversationContext conversationContext = cosplayContext.getConversationContext(conversationContextId);
        Conversation conversation = conversationContext.getConversation(conversationCode);

        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        return mixChatKit.chatStream(SupportedModelEnum.QwenTurbo, req -> {

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
                                             作为主持人，你需要分析以上发言，判断是否还有未提及或值得讨论的内容，选择推进话题或准备结束讨论。
                                             如果你发现还有需要讨论的内容，请输出“继续讨论”并指出进一步讨论的方向。
                                             如果你觉得讨论已经足够充分，请输出“结束讨论”。
                                             """
                                                     .formatted(history.toString())
                                     )
                             );
                         })
                         .compose(response -> {
                             String textContent = response.getMessage().getTextContent();
                             logger.info("As HOST:\n" + textContent);

                             if (textContent.contains("结束讨论")) {
                                 return Future.succeededFuture(EndScene.class.getName());
                             }

                             conversation.addSpeech(new Speech().setActorName("主持人").setContent(textContent));

                             return Future.succeededFuture(OneRoundScene.class.getName());
                         });
    }
}
