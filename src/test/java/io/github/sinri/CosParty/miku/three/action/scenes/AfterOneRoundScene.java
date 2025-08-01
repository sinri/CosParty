package io.github.sinri.CosParty.miku.three.action.scenes;

import io.github.sinri.CosParty.miku.MikuScene;
import io.github.sinri.CosParty.miku.three.action.DebateAction;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public class AfterOneRoundScene extends MikuScene {
    @Nonnull
    @Override
    protected Future<Void> playInner() {
        Integer roundCount = getCurrentContext().readInteger(DebateAction.FIELD_ROUND_COUNT);
        if (roundCount != null && roundCount >= 4) {
            // to conclude
            getCurrentContext().writeNumber(DebateAction.FIELD_END_FLAG, 1);
        } else {
            // continue debate
            getCurrentContext().writeNumber(DebateAction.FIELD_END_FLAG, 0);
        }
        return Future.succeededFuture();
    }
}
