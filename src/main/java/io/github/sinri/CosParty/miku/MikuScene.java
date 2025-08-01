package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayEngine;
import io.github.sinri.CosParty.kernel.CosplayScene;
import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

public abstract class MikuScene implements CosplayScene {
    private final String instanceId;
    private KeelIssueRecorder<KeelEventLog> logger;
    private CosplayContext currentContext;

    /**
     * All the subclasses should keep this non-parameter constructor.
     */
    public MikuScene() {
        super();
        this.instanceId = getSceneCode() + ":" + UUID.randomUUID();
    }

    @Nonnull
    @Override
    public final String getSceneCode() {
        return getClass().getName();
    }

    @Nonnull
    @Override
    public final String getInstanceId() {
        return instanceId;
    }

    @Override
    public Future<Void> initialize(@Nonnull CosplayEngine engine) {
        logger = engine.generateLogger();
        return Future.succeededFuture();
    }

    protected KeelIssueRecorder<KeelEventLog> getLogger() {
        Objects.requireNonNull(logger);
        return logger;
    }

    @Nonnull
    @Override
    public final Future<Void> play(@Nonnull CosplayEngine engine) {
        this.currentContext = engine.getCurrentContext();
        return playInner();
    }

    @Nonnull
    abstract protected Future<Void> playInner();

    @Nonnull
    protected final CosplayContext getCurrentContext() {
        Objects.requireNonNull(currentContext);
        return currentContext;
    }
}
