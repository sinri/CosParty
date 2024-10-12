package io.github.sinri.CosParty.test;

import io.github.sinri.AiOnHttpMix.azure.openai.core.AzureOpenAIServiceMeta;
import io.github.sinri.AiOnHttpMix.dashscope.core.DashscopeServiceMeta;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.utils.SupportedModel;
import io.github.sinri.AiOnHttpMix.volces.core.VolcesServiceMeta;
import io.github.sinri.keel.logger.KeelLogLevel;
import io.github.sinri.keel.tesuto.KeelTest;
import io.vertx.core.Future;
import org.jetbrains.annotations.NotNull;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class AnySample extends KeelTest {
    private AnyLLMKit anyLLMKit;

    protected AnyLLMKit getAnyLLMKit() {
        return anyLLMKit;
    }

    @Override
    protected @NotNull Future<Void> starting() {
        return super.starting()
                .compose(v -> {
                    Keel.getConfiguration().loadPropertiesFile("config.properties");
                    Keel.getLogger().setVisibleLevel(KeelLogLevel.NOTICE);
//                    AigcMix.enableVerboseLogger(KeelLogLevel.DEBUG);
                    anyLLMKit = initializeWithChatGPT("gpt-4-o");
//                    anyLLMKit = initializeWithQwen(SupportedModel.QwenPlus);
//                    anyLLMKit = initializeWithVolces("doubao-pro-128k");
                    return Future.succeededFuture();
                });
    }

    private AnyLLMKit initializeWithChatGPT(String serviceName) {
        String apiKey = Keel.config("azure.openai." + serviceName + ".apiKey");
        String resourceName = Keel.config("azure.openai." + serviceName + ".resourceName");
        String deployment = Keel.config("azure.openai." + serviceName + ".deployment");
        String apiVersion = Keel.config("azure.openai." + serviceName + ".apiVersion");

        var serviceMeta = new AzureOpenAIServiceMeta(apiKey, resourceName, deployment, apiVersion);
        var anyLLMKit = new AnyLLMKit();
        anyLLMKit.useChatGPT(serviceMeta);
        return anyLLMKit;
    }

    private AnyLLMKit initializeWithQwen(SupportedModel model) {
        String apiKey = Keel.config("dashscope.api_key");
        DashscopeServiceMeta serviceMeta = new DashscopeServiceMeta(apiKey);
        var anyLLMKit = new AnyLLMKit();
        anyLLMKit.useQwen(serviceMeta, model);
        return anyLLMKit;
    }

    private AnyLLMKit initializeWithVolces(String serviceName) {
        String apiKey = Keel.config("volces." + serviceName + ".apiKey");
        String model = Keel.config("volces." + serviceName + ".model");

        VolcesServiceMeta volcesServiceMeta = new VolcesServiceMeta(apiKey, model);
        var anyLLMKit = new AnyLLMKit();
        anyLLMKit.useVolces(volcesServiceMeta);
        return anyLLMKit;
    }
}
