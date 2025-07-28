package io.github.sinri.CosParty.facade;

import io.github.sinri.AiOnHttpMix.mix.chat.MixChatKit;
import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.keel.core.verticles.KeelVerticleImpl;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * The engine to run the AI workflow ordered by the {@link CosplayScript}.
 */
public abstract class CosplayEngine extends KeelVerticleImpl {
    @Nonnull
    private final CosplayScript cosplayScript;
    protected CosplayContext cosplayContext;
    /**
     * To be initialized before start as verticle; such as in {@link CosplayEngine#initialize()} method.
     */
    protected MixChatKit mixChatKit;
    /**
     * Initialized with {@link  io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter#outputCenter()} by
     * default.
     * Rewrite in {@link CosplayEngine#initialize()} if you want.
     */
    @Nonnull
    protected KeelIssueRecordCenter issueRecordCenter;

    public CosplayEngine(@Nonnull CosplayScript cosplayScript) {
        this.cosplayScript = cosplayScript;
        issueRecordCenter = KeelIssueRecordCenter.outputCenter();
    }

    /**
     * Initialize whatever you need for the execution.
     *
     * @return the future about the initialization accomplishment
     */
    abstract protected Future<Void> initialize();

    @Nonnull
    protected final CosplayScript getCosplayScript() {
        return this.cosplayScript;
    }

    @Nonnull
    public final CosplayContext getCosplayContext() {
        Objects.requireNonNull(cosplayContext);
        return cosplayContext;
    }

    @Nonnull
    public final KeelIssueRecordCenter getIssueRecordCenter() {
        return issueRecordCenter;
    }

    /**
     * @return the initialized {@link MixChatKit} field, which should not be null.
     */
    public final MixChatKit getMixChatKit() {
        Objects.requireNonNull(mixChatKit);
        return mixChatKit;
    }

    protected Future<Void> runAfterInitialization() {
        CosplayScene startingScene = cosplayScript.getStartingScene();
        AtomicReference<CosplayScene> currentScene = new AtomicReference<>(startingScene);
        return Keel.asyncCallRepeatedly(repeatedlyCallTask -> {
            CosplayScene scene = currentScene.get();
            if (scene == null) {
                repeatedlyCallTask.stop();
                return Future.succeededFuture();
            }
            return scene.play(this)
                        .compose(nextSceneCode -> {
                            if (nextSceneCode == null) {
                                currentScene.set(null);
                            } else {
                                CosplayScene sceneByCode = this.getCosplayScript()
                                                               .getSceneByCode(nextSceneCode);
                                currentScene.set(sceneByCode);
                            }
                            return Future.succeededFuture();
                        });
        });
    }

    /**
     * 使用verticle托管运行，运行完毕后自动undeploy。
     */
    @Override
    protected final Future<Void> startVerticle() {
        return this.initialize()
                   .compose(initialized -> {
                       runAfterInitialization()
                               .eventually(this::undeployMe);
                       return Future.succeededFuture();
                   });
    }

    /**
     * 不使用verticle托管，直接初始化执行，一般用在较短的任务上。
     */
    public Future<Void> swift() {
        return this.initialize()
                   .compose(initialized -> {
                       return runAfterInitialization()
                               .eventually(this::undeployMe);
                   });
    }
}
