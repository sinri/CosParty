package io.github.sinri.CosParty.model.discuss;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseChoice;
import io.github.sinri.CosParty.actor.AbstractActor;
import io.github.sinri.CosParty.actor.Action;
import io.vertx.core.Future;

import java.util.List;

public abstract class DiscussMember extends AbstractActor {
    public DiscussMember(AnyLLMKit anyLLMKit) {
        super(anyLLMKit);
    }

    @Override
    public String getSystemPrompt() {
        return "你是【" + getActorName() + "】，正在参加这一场讨论。\n\n" +
                "你的立场、观点和利益相关的信息如下：\n" + getContention() + "\n\n" +
                "需要根据会话历史继续会话，不要重复之前会话中已有的内容，每次回答不要超过200字。" +
                "讨论的结论将由讨论召集人作出，请不要僭越。\n"
                + getAdditionalRule();
    }

    abstract public String getContention();

    abstract public String getAdditionalRule();

    @Override
    public Future<String> act(List<Action> context) {
        return this.applyToLLMWithPrompt(
                        context,
                        "请作为【" + getActorName() + "】继续发表意见。"
                )
                .compose(response -> {
                    // 大概率返回文本
                    List<AnyLLMResponseChoice> choices = response.getChoices();
                    AnyLLMResponseChoice anyLLMResponseChoice = choices.get(0);
                    return Future.succeededFuture(anyLLMResponseChoice.getContent());
                });
    }
}
