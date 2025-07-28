package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.facade.CosplayEngine;
import io.github.sinri.CosParty.facade.CosplayScene;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

public abstract class MikuScene implements CosplayScene {
    @Nonnull
    private final String sceneCode;

    public MikuScene(@Nonnull String sceneCode) {
        this.sceneCode = sceneCode;
    }

    @Nonnull
    @Override
    public String getSceneCode() {
        return sceneCode;
    }

    @Nonnull
    @Override
    public final Future<String> play(@Nonnull CosplayEngine cosplayEngine) {
        KeelIssueRecorder<KeelEventLog> logger = cosplayEngine
                .getIssueRecordCenter()
                .generateIssueRecorder("MikuScene", () -> {
                    return new KeelEventLog()
                            .context(ctx -> ctx.put("scene_code", sceneCode));
                });
        return playInner(cosplayEngine, logger);
    }

    @Nonnull
    abstract protected Future<String> playInner(@Nonnull CosplayEngine cosplayEngine, @Nonnull KeelIssueRecorder<KeelEventLog> logger);
}
