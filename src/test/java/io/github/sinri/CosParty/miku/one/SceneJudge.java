package io.github.sinri.CosParty.miku.one;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.miku.MikuScene;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class SceneJudge extends MikuScene {

    public SceneJudge() {
        super();
    }

    @Nonnull
    @Override
    protected Future<Void> playInner() {
        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        var firstAnswer = getCurrentContext().readString("first_answer");
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
                             getLogger().info("second_answer: " + textContent);
                             getCurrentContext().writeString("second_answer", textContent);
                             return Future.succeededFuture(null);
                         });
    }
}
