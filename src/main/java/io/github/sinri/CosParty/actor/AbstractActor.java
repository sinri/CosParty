package io.github.sinri.CosParty.actor;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMRequest;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponse;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractActor implements Actor {
    private final AnyLLMKit anyLLMKit;

    public AbstractActor(AnyLLMKit anyLLMKit) {
        this.anyLLMKit = anyLLMKit;
    }

    @Override
    public AnyLLMKit getAnyLLMKit() {
        return anyLLMKit;
    }

    protected final Future<AnyLLMResponse> applyToLLMDirectly(List<Action> context) {
        return applyToLLM(context, null, req -> {
        });
    }

    protected final Future<AnyLLMResponse> applyToLLMWithPrompt(List<Action> context, @Nullable String content) {
        return applyToLLM(context, content, req -> {
        });
    }

    public final Future<AnyLLMResponse> applyToLLM(
            @Nullable List<Action> context,
            @Nullable String content,
            @Nullable Handler<AnyLLMRequest> requestHandler
    ) {
        Handler<AnyLLMRequest> handler = req -> {
            req.addSystemMessage(getSystemPrompt());
            if (context != null) {
                context.forEach(contextItem -> {
                    String msg = contextItem.message();
                    String actorName = contextItem.actorName();
                    req.addUserMessage("【" + actorName + "】：" + msg);
                });
            }
            if (content != null) {
                req.addUserMessage(content);
            }
            if (requestHandler != null) {
                requestHandler.handle(req);
            }
        };
        if (useStreamRequestToLLMApi()) {
            return getAnyLLMKit().requestWithStreamBuffer(handler);
        } else {
            return getAnyLLMKit().request(handler);
        }
    }

    protected boolean useStreamRequestToLLMApi() {
        return false;
    }
}
