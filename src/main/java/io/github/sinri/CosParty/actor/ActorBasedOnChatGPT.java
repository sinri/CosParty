package io.github.sinri.CosParty.actor;

import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.ChatGPTKit;
import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.message.AssistantMessage;
import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.message.SystemMessage;
import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.request.OpenAIChatGptRequest;
import io.github.sinri.AiOnHttpMix.azure.openai.core.AzureOpenAIServiceMeta;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public abstract class ActorBasedOnChatGPT implements Actor {
    private final ChatGPTKit chatGPTKit;
    private final AzureOpenAIServiceMeta serviceMeta;

    public ActorBasedOnChatGPT() {
        chatGPTKit = new ChatGPTKit();

        var serviceName = "gpt-4-o";
        String apiKey = Keel.config("azure.openai." + serviceName + ".apiKey");
        String resourceName = Keel.config("azure.openai." + serviceName + ".resourceName");
        String deployment = Keel.config("azure.openai." + serviceName + ".deployment");
        String apiVersion = Keel.config("azure.openai." + serviceName + ".apiVersion");

        serviceMeta = new AzureOpenAIServiceMeta(apiKey, resourceName, deployment, apiVersion);
    }

    @Override
    public AzureOpenAIServiceMeta getServiceMeta() {
        return serviceMeta;
    }

    @Override
    public ChatGPTKit getChatGPTKit() {
        return chatGPTKit;
    }

    protected final Future<AssistantMessage> applyToLLMDirectly(List<ContextItem> context) {
        return applyToLLM(context, req -> {
        });
    }

    protected final Future<AssistantMessage> applyToLLMWithPrompt(List<ContextItem> context, @Nullable String content) {
        return applyToLLM(context, req -> {
            if (content != null) {
                req.addMessage(b -> b.user(content));
            }
        });
    }

    @Override
    public final Future<AssistantMessage> applyToLLM(
            List<ContextItem> context,
            @Nullable Handler<OpenAIChatGptRequest> requestHandler
    ) {
        String requestId = UUID.randomUUID().toString();
        return getChatGPTKit().chatStream(
                getServiceMeta(),
                req -> {
                    req.addMessage(new SystemMessage(getSystemPrompt()));
                    context.forEach(contextItem -> {
                        String msg = contextItem.message();
                        String actorName = contextItem.actorName();
                        req.addMessage(b -> b.user("【" + actorName + "】：" + msg));
                    });
                    if (requestHandler != null) {
                        requestHandler.handle(req);
                    }
                },
                requestId
        )
//                .compose(x -> {
//                    var c = x.getContent();
//                    return Future.succeededFuture(c);
//                })
                ;
    }
}
