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

/**
 * Miku动作抽象基类。
 * <p>
 * 提供动作的基本实现，包括场景缓存、上下文管理和日志记录功能。
 * 子类需要实现具体的业务逻辑和场景初始化。
 */
public abstract class MikuAction implements CosplayAction {
    // private final Map<String, CosplayScene> cachedSceneMap;
    private final Set<String> sceneCodeSet;
    private final String instanceId;
    private CosplayContext actionContext;
    private KeelIssueRecorder<KeelEventLog> logger;

    public MikuAction() {
        super();

        this.sceneCodeSet = new HashSet<>();
        initializeRelatedScenes().forEach(scene -> {
            this.sceneCodeSet.add(scene.getName());
        });

        this.instanceId = getSceneCode() + ":" + UUID.randomUUID();
    }

    /**
     * 构建一个列表，包含Action内涉及的所有Scene的实例。
     * 在本类的构造方法中运行。
     */
    abstract protected List<Class<? extends CosplayScene>> initializeRelatedScenes();

    @Nonnull
    @Override
    public final CosplayContext getContextOnThisActionScope() {
        Objects.requireNonNull(actionContext);
        return actionContext;
    }

    /**
     * 为当前动作作用域构建上下文。
     * <p>
     * 子类必须实现此方法来提供动作专用的上下文实例。
     */
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
        if (!this.sceneCodeSet.contains(nextSceneCodeInAction)) {
            throw new IllegalArgumentException("Scene Code [%s] not found".formatted(nextSceneCodeInAction));
        }
        return MikuEngine.loadSceneByCode(nextSceneCodeInAction, CosplayScene.class);
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
