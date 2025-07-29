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

public class EndScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<String> playInner(@Nonnull CosplayContext cosplayContext, @Nonnull KeelIssueRecorder<KeelEventLog> logger) {
        String conversationCode = cosplayContext.readString(DiscussionScript.FIELD_CONVERSATION_CODE);
        Integer conversationContextId = cosplayContext.readInteger(DiscussionScript.FIELD_CONVERSATION_CONTEXT_ID);
        ConversationContext conversationContext = cosplayContext.getConversationContext(conversationContextId);
        Conversation conversation = conversationContext.getConversation(conversationCode);

        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        return mixChatKit.chatStream(SupportedModelEnum.QwenMax, req -> {

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
                                             作为主持人，你需要总结以上发言，并在分析各方意见的基础上形成讨论的结论。
                                             """
                                                     .formatted(history.toString())
                                     )
                             );
                         })
                         .compose(response -> {
                             String textContent = response.getMessage().getTextContent();
                             logger.info("HOST Conclusion:\n" + textContent);

                             return Future.succeededFuture(null);
                         });
    }
}
