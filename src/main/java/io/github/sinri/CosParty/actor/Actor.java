package io.github.sinri.CosParty.actor;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.vertx.core.Future;

import java.util.List;

public interface Actor {
    AnyLLMKit getAnyLLMKit();

    String getSystemPrompt();

    String getActorName();

    /**
     * 决定是否发言以及发言的内容。
     * 在得出最终发言过程中，可以执行任务、多次查询、反复会话等。
     *
     * @return Future of response text, which is nullable.
     */
    Future<String> act(List<Action> context);

}
