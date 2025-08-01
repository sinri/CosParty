package io.github.sinri.CosParty.miku.three;

import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class MotionConclusionScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<Void> playInner() {
        String debateConclusion = getCurrentContext().readString("ACTION@" + DebateAction.FIELD_DEBATE_CONCLUSION);
        getLogger().info("Debate Conclusion: " + debateConclusion);
        return Future.succeededFuture();
    }
}
