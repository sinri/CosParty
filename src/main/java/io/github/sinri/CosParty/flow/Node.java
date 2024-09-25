package io.github.sinri.CosParty.flow;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponse;
import io.vertx.core.Future;

public interface Node {
    String getNodeName();

    Future<Void> prepareForActing(Flow flow);

    Flow getFlow();


    Future<AnyLLMResponse> act();

    /**
     * @return Future of Node to move to, or Future of null to finish.
     */
    Future<Node> loadNextNodeToGo();
}
