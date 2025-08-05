package io.github.sinri.CosParty.miku.three;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.chat.request.MixChatRequestExtra;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class RandomMotionScene extends MikuScene {
    @Nonnull
    @Override
    public Future<Void> play() {
        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        var subject = context().readString("subject");

        return mixChatKit.chatStream(SupportedModelEnum.QwenPlus, req -> {
                             req.addMessage(msg -> msg
                                     .setRole("user")
                                     .setTextContent("根据以下主题“%s”，结合中国特色社会主义新时代主旋律发展方向，出一个符合大学生辩论赛场景的辩题，直接输出。"
                                             .formatted(subject)
                                     ));
                             req.setExtra(MixChatRequestExtra.create()
                                                             .setTemperature(1.6f)
                                                             .setSeed(Keel.randomHelper().getPRNG().nextInt()));
                         })
                         .compose(response -> {
                             String textContent = response.getMessage().getTextContent();

                             getLogger().info("RANDOM MOTION: " + textContent);

                             getLogger().info("currentContext is " + context().getContextId());
                             getLogger().info("key: " + "ACTION@" + DebateAction.FIELD_DEBATE_MOTION);
                             context().writeString("ACTION@" + DebateAction.FIELD_DEBATE_MOTION, textContent);
                             return Future.succeededFuture();
                         });
    }
}
