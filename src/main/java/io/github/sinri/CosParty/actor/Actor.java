package io.github.sinri.CosParty.actor;

import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.ChatGPTKit;
import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.message.AssistantMessage;
import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.request.OpenAIChatGptRequest;
import io.github.sinri.AiOnHttpMix.azure.openai.core.AzureOpenAIServiceMeta;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Actor {
    AzureOpenAIServiceMeta getServiceMeta();

    ChatGPTKit getChatGPTKit();

    String getSystemPrompt();

    String getActorName();

    /**
     * 决定是否发言。
     * 主导的Actor不应沉默。
     */
    Future<String> thinkAndSpeak(List<ContextItem> context);

    Future<AssistantMessage> applyToLLM(List<ContextItem> context, @Nullable Handler<OpenAIChatGptRequest> requestHandler);

    record ContextItem(String actorName, String message) {
        @Override
        public String toString() {
            return "ContextItem{" + actorName + ":" + message + '}';
        }
    }
}
