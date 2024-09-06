package io.github.sinri.CosParty.model.debate;

import io.github.sinri.CosParty.actor.ActorBasedOnChatGPT;
import io.vertx.core.Future;

import java.util.List;

public abstract class Debater extends ActorBasedOnChatGPT {
    @Override
    public String getSystemPrompt() {
        return "你是【" + getActorName() + "】，正在参加这一场辩论。\n" +
                "你的观点为：" + getContention() + "\n" +
                "需要根据会话历史继续会话，不要重复之前会话中已有的内容。\n"
                + getAdditionalRule();
    }

    @Override
    abstract public String getActorName();

    abstract public String getContention();

    abstract public String getAdditionalRule();

    @Override
    public Future<String> thinkAndSpeak(List<ContextItem> context) {
        return this.applyToLLMDirectly(context)
                .compose(am -> {
                    // 大概率返回文本
                    return Future.succeededFuture(am.getContent());
                });
    }

}
