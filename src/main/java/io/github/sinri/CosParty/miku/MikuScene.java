package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.facade.CosplayEngine;
import io.github.sinri.CosParty.facade.CosplayScene;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public abstract class MikuScene implements CosplayScene {
    /**
     * All the subclasses should keep this non-parameter constructor.
     */
    public MikuScene() {
        super();
    }

    @Nonnull
    @Override
    public final String getSceneCode() {
        return getClass().getName();
    }

    @Nonnull
    @Override
    public final Future<String> play(@Nonnull CosplayEngine cosplayEngine) {
        KeelIssueRecorder<KeelEventLog> logger = cosplayEngine
                .getIssueRecordCenter()
                .generateIssueRecorder(
                        "MikuScene",
                        () -> new KeelEventLog()
                                .context(ctx -> ctx.put("scene_code", getSceneCode()))
                );
        return playInner(cosplayEngine.getCosplayContext(), logger);
    }

    @Nonnull
    abstract protected Future<String> playInner(
            @Nonnull CosplayContext cosplayContext,
            @Nonnull KeelIssueRecorder<KeelEventLog> logger
    );
}
