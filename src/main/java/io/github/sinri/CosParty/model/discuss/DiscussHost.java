package io.github.sinri.CosParty.model.discuss;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseChoice;
import io.github.sinri.CosParty.actor.AbstractActor;
import io.github.sinri.CosParty.actor.Action;
import io.vertx.core.Future;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class DiscussHost extends AbstractActor {
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
        if (isToStop()) {
            return actToStop(context);
        }
        if (getGuideWord() != null && !getGuideWord().isBlank()) {
            context.add(new Action(this.getActorName(), getGuideWord()));
        }
        return Future.succeededFuture(null);
    }

    /**
     * 如果需要动用FC，那就重载。
     */
    protected Future<String> actToStop(List<Action> context) {
        var s = "本场讨论已经结束，请综合上面的讨论，给出总结和最终的结论。";
        return this.applyToLLMWithPrompt(context, s)
                .compose(response -> {
                    List<AnyLLMResponseChoice> choices = response.getChoices();
                    AnyLLMResponseChoice anyLLMResponseChoice = choices.get(0);
                    return Future.succeededFuture(anyLLMResponseChoice.getContent());
                });
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
