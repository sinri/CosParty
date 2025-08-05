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

public class AfterOneRoundScene extends MikuScene implements DiscussionContextMixin {
    @Nonnull
    @Override
    public Future<Void> play() {
        Conversation conversation;
        Integer roundCount = roundCount();
        if (Objects.requireNonNullElse(roundCount, 0) > 3) {
            return Future.succeededFuture();
        }

        String conversationCode = conversationCode();
        Integer conversationContextId = conversationContextId();
        Objects.requireNonNull(conversationContextId);
        ConversationContext conversationContext = context().getConversationContext(conversationContextId);
        conversation = conversationContext.getConversation(conversationCode);
        Objects.requireNonNull(conversation);

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
                             getLogger().info("Actor Spoke", ctx -> ctx
                                     .put("actor", "HOST")
                                     .put("content", textContent));

                             if (textContent.contains("结束讨论")) {
                                 endFlag(1);
                                 return Future.succeededFuture();
                             }

                             conversation.addSpeech(new Speech().setActorName("主持人").setContent(textContent));

                             return Future.succeededFuture();
                         });
    }
}
