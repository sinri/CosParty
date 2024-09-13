package io.github.sinri.CosParty.model.discuss;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseChoice;
import io.github.sinri.CosParty.actor.Action;
import io.github.sinri.CosParty.actor.ActorBasedOnChatGPT;
import io.vertx.core.Future;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class DiscussHost extends ActorBasedOnChatGPT {
    private boolean toStop = false;
    private @Nullable String guideWord;

    public DiscussHost(AnyLLMKit anyLLMKit) {
        super(anyLLMKit);
    }

    public abstract String getDiscussTopic();

    public abstract String getDiscussIntroduction();

    @Override
    public String getSystemPrompt() {
        return "你是这次讨论的召集人。";
    }

    @Override
    public String getActorName() {
        return "讨论召集人";
    }

    abstract public Future<Boolean> shouldStopDiscuss(List<Action> context);

    @Override
    public Future<String> act(List<Action> context) {
        String s;
        if (isToStop()) {
            s = "本场讨论已经结束，请综合上面的讨论，给出总结和最终的结论。";
        } else {
            if (getGuideWord() != null && !getGuideWord().isBlank()) {
                context.add(new Action(this.getActorName(), getGuideWord()));
            }
            s = null;
        }
        if (s != null) {
            return this.applyToLLMWithPrompt(context, s)
                    .compose(response -> {
                        List<AnyLLMResponseChoice> choices = response.getChoices();
                        AnyLLMResponseChoice anyLLMResponseChoice = choices.get(0);
                        return Future.succeededFuture(anyLLMResponseChoice.getContent());
                    });
        } else {
            return Future.succeededFuture(null);
        }
    }

    public boolean isToStop() {
        return toStop;
    }

    public DiscussHost setToStop(boolean toStop) {
        this.toStop = toStop;
        return this;
    }

    public @Nullable String getGuideWord() {
        return guideWord;
    }

    public DiscussHost setGuideWord(@Nullable String guideWord) {
        this.guideWord = guideWord;
        return this;
    }
}
