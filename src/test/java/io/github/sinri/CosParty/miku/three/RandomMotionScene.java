package io.github.sinri.CosParty.miku.three;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.chat.request.MixChatRequestExtra;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.UUID;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class RandomMotionScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<Void> playInner() {
        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        return mixChatKit.chatStream(SupportedModelEnum.QwenPlus, req -> {
                             req.addMessage(msg -> msg
                                     .setRole("user")
                                     .setTextContent("根据以下神秘字符串“%s”进行冥想，出一个符合大学生辩论赛场景的辩题，直接输出。"
                                             .formatted(UUID.randomUUID().toString())
                                     ));
                             req.setExtra(MixChatRequestExtra.create()
                                                             .setTemperature(1.6f)
                                                             .setSeed(Keel.randomHelper().getPRNG().nextInt()));
                         })
                         .compose(response -> {
                             String textContent = response.getMessage().getTextContent();

                             getLogger().info("RANDOM MOTION: " + textContent);

                             getLogger().info("currentContext is " + getCurrentContext().getContextId());
                             getLogger().info("key: " + "ACTION@" + DebateAction.FIELD_DEBATE_MOTION);
                             getCurrentContext().writeString("ACTION@" + DebateAction.FIELD_DEBATE_MOTION, textContent);
                             return Future.succeededFuture();
                         });
    }
}
