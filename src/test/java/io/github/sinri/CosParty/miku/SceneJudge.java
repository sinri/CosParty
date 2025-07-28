package io.github.sinri.CosParty.miku;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.facade.CosplayEngine;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

class SceneJudge extends MikuScene {

    public SceneJudge() {
        super(SampleMikuScript.judgeSceneCode);
    }


    @Nonnull
    @Override
    protected Future<String> playInner(@Nonnull CosplayEngine cosplayEngine, @Nonnull KeelIssueRecorder<KeelEventLog> logger) {
        CosplayContext cosplayContext = cosplayEngine.getCosplayContext();
        MixChatKit mixChatKit = cosplayEngine.getMixChatKit();
        return cosplayContext.readString("first_answer")
                             .compose(firstAnswer -> {
                                 return mixChatKit.chatStream(
                                                          SupportedModelEnum.QwenPlus,
                                                          req -> req
                                                                  .addMessage(msg -> msg
                                                                          .setRole("system")
                                                                          .setTextContent("根据用户的输入，检查语法并翻译成中文")
                                                                  )
                                                                  .addMessage(msg -> msg
                                                                          .setRole("user")
                                                                          .setTextContent(firstAnswer)
                                                                  )
                                                  )
                                                  .compose(resp -> {
                                                      String textContent = resp.getMessage().getTextContent();
                                                      logger.info("second_answer: " + textContent);
                                                      cosplayContext.writeString("second_answer", textContent);
                                                      return Future.succeededFuture(null);
                                                  });
                             });
    }
}
