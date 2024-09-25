package io.github.sinri.CosParty.test.flow;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.CosParty.actor.Action;
import io.github.sinri.CosParty.flow.Flow;
import io.github.sinri.CosParty.flow.Node;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class FlowSample implements Flow {
    /*
    1. 分析用户需求，是问商品价格（2）还是要针对订单售后（3）。
    2. 根据用户描述，查询出指定的商品和对应的价格。
    3. 根据用户的描述，查询出订单。
     */
    public static final String ACTOR_CUSTOMER = "客户";
    private final List<Action> context;
    private final AnyLLMKit anyLLMKit;
    private Node startNode;

    public FlowSample(AnyLLMKit anyLLMKit) {
        context = new ArrayList<>();
        this.anyLLMKit = anyLLMKit;
    }

    @Override
    public Flow setStartNode(Node startNode) {
        this.startNode = startNode;
        return this;
    }

    @Override
    public Node getStartNode() {
        return this.startNode;
    }

    @Override
    public AnyLLMKit getAnyLLMKit() {
        return anyLLMKit;
    }

    @Override
    public Future<Void> whenFlowFinished() {
        Keel.getLogger().fatal("io.github.sinri.CosParty.test.flow.FlowToSale.whenFlowFinished");
        return Future.succeededFuture();
    }
}
