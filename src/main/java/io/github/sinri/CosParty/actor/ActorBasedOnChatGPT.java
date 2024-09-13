package io.github.sinri.CosParty.actor;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMRequest;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponse;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ActorBasedOnChatGPT implements Actor {
    private final AnyLLMKit anyLLMKit;

    public ActorBasedOnChatGPT(AnyLLMKit anyLLMKit) {
        this.anyLLMKit = anyLLMKit;
    }

    @Override
    public AnyLLMKit getAnyLLMKit() {
        return anyLLMKit;
    }

    protected final Future<AnyLLMResponse> applyToLLMDirectly(List<Action> context) {
        return applyToLLM(context, req -> {
        });
    }

    protected final Future<AnyLLMResponse> applyToLLMWithPrompt(List<Action> context, @Nullable String content) {
        return applyToLLM(context, req -> {
            if (content != null) {
                req.addUserMessage(content);
            }
        });
    }

    public final Future<AnyLLMResponse> applyToLLM(
            List<Action> context,
            @Nullable Handler<AnyLLMRequest> requestHandler
    ) {
        return getAnyLLMKit().requestWithStreamBuffer(req -> {
            req.addSystemMessage(getSystemPrompt());
                    context.forEach(contextItem -> {
                        String msg = contextItem.message();
                        String actorName = contextItem.actorName();
                        req.addUserMessage("【" + actorName + "】：" + msg);
                    });
                    if (requestHandler != null) {
                        requestHandler.handle(req);
                    }
        })
//                .onSuccess(anyLLMResponse -> {
//                    Keel.getLogger().fatal("anyLLMResponse:" + anyLLMResponse.toString());
//                })
                ;
    }
}
