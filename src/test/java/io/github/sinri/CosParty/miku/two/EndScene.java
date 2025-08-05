package io.github.sinri.CosParty.miku.two;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.kernel.context.conversation.Conversation;
import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;
import io.github.sinri.CosParty.kernel.context.conversation.Speech;
import io.github.sinri.CosParty.miku.MikuScene;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EndScene extends MikuScene implements DiscussionContextMixin {
    @Nonnull
    @Override
    public Future<Void> play() {
        Conversation conversation;
        String conversationCode = conversationCode();
        Integer conversationContextId = conversationContextId();
        Objects.requireNonNull(conversationContextId);
        ConversationContext conversationContext = context().getConversationContext(conversationContextId);
        conversation = conversationContext.getConversation(conversationCode);
        Objects.requireNonNull(conversation);
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
                             getLogger().info("HOST Conclusion:\n" + textContent);

                             return Future.succeededFuture(null);
                         });
    }
}
