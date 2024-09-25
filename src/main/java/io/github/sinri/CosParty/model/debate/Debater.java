package io.github.sinri.CosParty.model.debate;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseChoice;
import io.github.sinri.CosParty.actor.AbstractActor;
import io.github.sinri.CosParty.actor.Action;
import io.vertx.core.Future;

import java.util.List;

public abstract class Debater extends AbstractActor {
    public Debater(AnyLLMKit anyLLMKit) {
        super(anyLLMKit);
    }

    @Override
    public String getSystemPrompt() {
        return "你是【" + getActorName() + "】，正在参加这一场辩论。\n" +
                "你的观点为：" + getContention() + "\n" +
                "需要根据会话历史继续会话，不要重复之前会话中已有的内容。\n"
                + getAdditionalRule();
    }

    abstract public String getContention();

    abstract public String getAdditionalRule();

    @Override
    public Future<String> act(List<Action> context) {
        return this.applyToLLMDirectly(context)
                .compose(response -> {
                    // 大概率返回文本
                    List<AnyLLMResponseChoice> choices = response.getChoices();
                    AnyLLMResponseChoice anyLLMResponseChoice = choices.get(0);
                    return Future.succeededFuture(anyLLMResponseChoice.getContent());
                });
    }

}
