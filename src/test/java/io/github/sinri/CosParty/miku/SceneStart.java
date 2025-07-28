package io.github.sinri.CosParty.miku;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

class SceneStart extends MikuScene {

    public SceneStart() {
        super();
    }

    @Nonnull
    @Override
    protected Future<String> playInner(@Nonnull CosplayContext cosplayContext, @Nonnull KeelIssueRecorder<KeelEventLog> logger) {
        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        return cosplayContext.readString("raw_question")
                             .compose(rawQuestion -> {
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
                                                      return Future.succeededFuture(SceneJudge.class.getName());
                                                  });
                             });
    }
}
