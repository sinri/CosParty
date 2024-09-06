package io.github.sinri.CosParty.model.debate;

import io.github.sinri.CosParty.actor.ActorBasedOnChatGPT;
import io.vertx.core.Future;

import java.util.List;

public abstract class DebateHost extends ActorBasedOnChatGPT {

    @Override
    public String getSystemPrompt() {
        return "你是这场辩论的主持人。";
    }

    @Override
    public String getActorName() {
        return "主持人";
    }

    public abstract String getDebateTopic();

    @Override
    public Future<String> thinkAndSpeak(List<ContextItem> context) {
        return this.applyToLLMWithPrompt(
                        context,
                        "现在辩论过程已结束，需要根据以上辩论内容做如下总结：\n" +
                                "1. 各方论点和论据的总结摘要；\n" +
                                "2. 各方的优劣势分析；\n" +
                                "3. 综合辩论情况，给出就此辩论主题主持人支持的那一方。"
                )
                .compose(am -> {
                    // 大概率返回文本
                    return Future.succeededFuture(am.getContent());
                });
    }

    abstract public Future<Boolean> shouldStopDebate(List<ContextItem> context);
}
