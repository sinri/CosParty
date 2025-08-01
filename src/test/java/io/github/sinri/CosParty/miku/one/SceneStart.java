package io.github.sinri.CosParty.miku.one;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.AiOnHttpMix.mix.service.NativeMixServiceAdapter;
import io.github.sinri.AiOnHttpMix.mix.service.SupportedModelEnum;
import io.github.sinri.CosParty.miku.MikuScene;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class SceneStart extends MikuScene {

    public SceneStart() {
        super();
    }

    @Nonnull
    @Override
    protected Future<Void> playInner() {
        NativeMixServiceAdapter adapter = new NativeMixServiceAdapter();
        MixChatKit mixChatKit = MixChatKit.create(adapter);

        var rawQuestion = getCurrentContext().readString("raw_question");
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
                             getCurrentContext().writeString("first_answer", textContent);
                             getLogger().info("first_answer: " + textContent);
                             return Future.succeededFuture();
                         });
    }
}
