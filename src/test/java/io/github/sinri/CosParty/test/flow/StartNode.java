package io.github.sinri.CosParty.test.flow;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponse;
import io.github.sinri.CosParty.flow.Flow;
import io.github.sinri.CosParty.flow.Node;
import io.vertx.core.Future;

public class StartNode implements Node {
    private final String customerDemand;
    private Flow flow;

    public StartNode(String customerDemand) {
        this.customerDemand = customerDemand;
    }

    @Override
    public String getNodeName() {
        return "销售人员接到用户咨询";
    }

    @Override
    public Future<Void> prepareForActing(Flow flow) {
        this.flow = flow;
        return Future.succeededFuture();
    }

    @Override
    public Flow getFlow() {
        return this.flow;
    }

    @Override
    public Future<AnyLLMResponse> act() {
        return getFlow().getAnyLLMKit().request(anyLLMRequest -> anyLLMRequest
                .addSystemMessage("你是一个网店的销售人员。")
                .addUserMessage("你接到了用户的以下咨询，请判断用户咨询的产品和所需的帮助，并调用相应的函数进行处理。\n\n" +
                        "用户咨询：\n" + customerDemand)
        );
    }

    @Override
    public Future<Node> loadNextNodeToGo() {
        return Future.succeededFuture();
    }
}
