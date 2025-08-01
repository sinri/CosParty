package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayAction;
import io.github.sinri.CosParty.kernel.CosplayEngine;
import io.github.sinri.CosParty.kernel.CosplayScene;
import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class MikuAction implements CosplayAction {
    private final Map<String, CosplayScene> cachedSceneMap;
    private final String instanceId;
    private CosplayContext actionContext;
    private KeelIssueRecorder<KeelEventLog> logger;

    public MikuAction() {
        super();

        this.cachedSceneMap = new HashMap<>();
        initializeRelatedScenes().forEach(scene -> {
            this.cachedSceneMap.put(scene.getSceneCode(), scene);
        });

        this.instanceId = getSceneCode() + ":" + UUID.randomUUID();
    }

    /**
     * 构建一个列表，包含Action内涉及的所有Scene的实例。
     * 在本类的构造方法中运行。
     */
    abstract protected List<CosplayScene> initializeRelatedScenes();

    @Nonnull
    @Override
    public final CosplayContext getContextOnThisActionScope() {
        Objects.requireNonNull(actionContext);
        return actionContext;
    }

    @Nonnull
    abstract protected CosplayContext buildContextForThisActionScope();

    @Nonnull
    @Override
    public final String getSceneCode() {
        return this.getClass().getName();
    }

    @Nonnull
    @Override
    public final String getInstanceId() {
        return instanceId;
    }

    @Nonnull
    @Override
    public final CosplayScene getSceneByCodeInAction(@Nonnull String nextSceneCodeInAction) {
        if (!this.cachedSceneMap.containsKey(nextSceneCodeInAction)) {
            throw new IllegalArgumentException("Scene Code [%s] not found".formatted(nextSceneCodeInAction));
        }
        return cachedSceneMap.get(nextSceneCodeInAction);
    }

    /**
     * This method is available in {@link MikuAction#play(CosplayEngine)} scope.
     *
     * @return the logger created in the beginning of {@link MikuAction#play(CosplayEngine)}
     */
    protected final KeelIssueRecorder<KeelEventLog> getLogger() {
        Objects.requireNonNull(logger);
        return logger;
    }

    @Override
    public Future<Void> initialize(@Nonnull CosplayEngine engine) {
        this.logger = engine.generateLogger();
        this.actionContext = buildContextForThisActionScope();
        return Future.succeededFuture();
    }

    @Nonnull
    @Override
    public final Future<Void> play(@Nonnull CosplayEngine engine) {
        return Future.succeededFuture();
    }

}
