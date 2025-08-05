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
    private CosplayEngine engine;
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
        this.engine = engine;
        logger = engine.generateLogger();
        this.currentContext = engine().getCurrentContext();
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
    public CosplayContext context() throws IllegalStateException {
        if(currentContext==null){
            throw new IllegalStateException();
        }
        return currentContext;
    }

    public final CosplayEngine engine() {
        return engine;
    }
}
