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

    public Future<AnyLLMResponse> applyToLLM(
            @Nullable List<Action> context,
            @Nullable String content,
            @Nullable Handler<AnyLLMRequest> requestHandler
    ) {
        return withLLM(req -> {
            req.addSystemMessage(getSystemPrompt());
            StringBuilder um = new StringBuilder("现有各方参与的对话如下。\n\n");
            if (context != null) {
                context.forEach(contextItem -> {
                    String msg = contextItem.message();
                    String actorName = contextItem.actorName();
                    //req.addUserMessage("【" + actorName + "】：" + msg);
                    um.append(actorName).append("的发言：\n").append(msg).append("\n");
                });
            }
            if (content != null) {
                //req.addUserMessage(content);
                um.append(content);
            } else {
                um.append("请在你所持有的立场、职责和能力范围内进行回复。");
            }
            req.addUserMessage(um.toString());
            if (requestHandler != null) {
                requestHandler.handle(req);
            }
        });
    }

    protected final Future<AnyLLMResponse> withLLM(@Nullable Handler<AnyLLMRequest> requestHandler) {
        if (useStreamRequestToLLMApi()) {
            return getAnyLLMKit().requestWithStreamBuffer(requestHandler);
        } else {
            return getAnyLLMKit().request(requestHandler);
        }
    }

    protected boolean useStreamRequestToLLMApi() {
        return false;
    }
}
