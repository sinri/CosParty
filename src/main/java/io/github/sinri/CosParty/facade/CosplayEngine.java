package io.github.sinri.CosParty.facade;

import io.github.sinri.CosParty.facade.context.CosplayContext;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * The engine to run the AI workflow ordered by the {@link CosplayScript}.
 */
public abstract class CosplayEngine {
    private final String engineId;
    @Nonnull
    private final CosplayScript cosplayScript;
    protected CosplayContext cosplayContext;
    /**
     * Initialized with {@link  io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter#outputCenter()} by
     * default.
     * Rewrite in {@link CosplayEngine#initialize()} if you want.
     */
    @Nonnull
    protected KeelIssueRecordCenter issueRecordCenter;

    public CosplayEngine(@Nonnull CosplayScript cosplayScript) {
        this.engineId = getClass().getName() + "@" + UUID.randomUUID();
        this.cosplayScript = cosplayScript;
        this.issueRecordCenter = KeelIssueRecordCenter.outputCenter();
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

    private Future<Void> runAfterInitialization() {
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
     * 不使用verticle托管，直接初始化执行，一般用在较短的任务上。
     */
    public Future<Void> swift(@Nonnull Map<String, String> inputMap) {
        return this.initialize()
                   .compose(initialized -> {
                       CosplayContext ctx = getCosplayContext();
                       for (var entry : inputMap.entrySet()) {
                           ctx.writeString(entry.getKey(), entry.getValue());
                       }

                       return runAfterInitialization();
                   });
    }

    public final String getEngineId() {
        return engineId;
    }
}
