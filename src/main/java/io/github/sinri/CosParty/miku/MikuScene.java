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

/**
 * Miku场景抽象基类。
 * <p>
 * 提供场景的基本实现，包括实例管理、日志记录和上下文访问功能。
 * 子类需要实现具体的业务逻辑。
 * <p>
 * 在Miku实现中，一个场景类的唯一标识为其类的唯一名称，因而只能对应一个场景实例。
 */
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

    /**
     * 获取场景专用的日志记录器。
     *
     * @return 日志记录器实例
     */
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

    /**
     * 执行场景的核心业务逻辑。
     * <p>
     * 子类必须实现此方法来提供具体的场景执行逻辑。
     */
    @Nonnull
    abstract protected Future<Void> playInner();

    /**
     * 获取当前场景的上下文。
     *
     * @return 当前上下文实例
     */
    @Nonnull
    protected final CosplayContext getCurrentContext() {
        Objects.requireNonNull(currentContext);
        return currentContext;
    }
}
