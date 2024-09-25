package io.github.sinri.CosParty.flow;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;

import java.util.concurrent.atomic.AtomicReference;

public interface Flow {

    Node getStartNode();

    Flow setStartNode(Node startNode);

    default Future<Void> run() {
        AtomicReference<Node> startNodeRef = new AtomicReference<>(getStartNode());
        return KeelAsyncKit.repeatedlyCall(routineResult -> {
                    var startNode = startNodeRef.get();
                    if (startNode == null) {
                        routineResult.stop();
                        return Future.succeededFuture();
                    }
                    return startNode.prepareForActing(this)
                            .compose(v -> {
                                return startNode.act();
                            })
                            .compose(resp -> {
                                return startNode.loadNextNodeToGo();
                            })
                            .compose(nextNode -> {
                                startNodeRef.set(nextNode);
                                return Future.succeededFuture();
                            });
                })
                .compose(v -> {
                    return this.whenFlowFinished();
                });
    }

    AnyLLMKit getAnyLLMKit();

    Future<Void> whenFlowFinished();
}
