package io.github.sinri.CosParty.miku;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.facade.CosplayEngine;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

class SceneStart extends MikuScene {
    @Nonnull
    private final String rawQuestion;

    public SceneStart(@Nonnull String rawQuestion) {
        super(SampleMikuScript.startingSceneCode);
        this.rawQuestion = rawQuestion;
    }

    @Nonnull
    @Override
    protected Future<String> playInner(@Nonnull CosplayEngine cosplayEngine, @Nonnull KeelIssueRecorder<KeelEventLog> logger) {
        CosplayContext cosplayContext = cosplayEngine.getCosplayContext();
        MixChatKit mixChatKit = cosplayEngine.getMixChatKit();

        cosplayContext.writeString("raw_question", rawQuestion);

        return mixChatKit.chatStream(
                                 SupportedModelEnum.QwenPlus,
                                 req -> req
                                         .addMessage(msg -> msg
                                                 .setRole("system")
                                                 .setTextContent("You are Hatsune Miku. Answer the questions from your fans in Japanese.")
                                         )
                                         .addMessage(msg -> msg
                                                 .setRole("user")
                                                 .setTextContent(rawQuestion)
                                         )
                         )
                         .compose(resp -> {
                             String textContent = resp.getMessage().getTextContent();
                             cosplayContext.writeString("first_answer", textContent);
                             logger.info("first_answer: " + textContent);
                             return Future.succeededFuture(SampleMikuScript.judgeSceneCode);
                         });
    }
}
