package io.github.sinri.CosParty.miku.three.action.scenes;

import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class AfterOneRoundScene extends MikuScene {
    @Nonnull
    @Override
    public Future<Void> play() {
        Integer roundCount = context().readInteger(DebateAction.FIELD_ROUND_COUNT);
        if (roundCount != null && roundCount >= 4) {
            // to conclude
            context().writeNumber(DebateAction.FIELD_END_FLAG, 1);
        } else {
            // continue debate
            context().writeNumber(DebateAction.FIELD_END_FLAG, 0);
        }
        return Future.succeededFuture();
    }
}
