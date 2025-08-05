package io.github.sinri.CosParty.kernel;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * 角色扮演引擎抽象基类。
 * <p>
 * 负责执行角色扮演脚本并管理场景流转，协调脚本、场景和上下文之间的交互。
 * 支持场景栈管理、上下文传递和场景跳转。
 */
public abstract class CosplayEngine {
    private final String engineId;

    @Nonnull
    private final CosplayScript script;
    private final Stack<CosplayAction> actionStack;
    private final AtomicReference<CosplayScene> currentSceneRef = new AtomicReference<>();
    protected CosplayContext contextOnScriptScope;
    @Nonnull
    protected KeelIssueRecordCenter issueRecordCenter;
    private KeelIssueRecorder<KeelEventLog> logger;

    /**
     * 构造角色扮演引擎实例。
     *
     * @param script 要执行的角色扮演脚本
     */
    public CosplayEngine(@Nonnull CosplayScript script) {
        this.engineId = getClass().getName() + "@" + UUID.randomUUID();
        this.script = script;
        this.issueRecordCenter = buildIssueRecordCenter();
        this.actionStack = new Stack<>();
    }

    /**
     * 在引擎初始化时会调用本方法初始化本类应利用的日志记录中心。
     *
     * @return 引擎利用的日志记录中心
     */
    @Nonnull
    protected KeelIssueRecordCenter buildIssueRecordCenter() {
        return KeelIssueRecordCenter.outputCenter();
    }

    /**
     * 初始化引擎。
     * <p>
     * 子类必须实现此方法来提供具体的初始化逻辑。
     *
     * @return 初始化完成的Future
     */
    abstract protected Future<Void> initialize();

    /**
     * 获取当前引擎执行的脚本。
     *
     * @return 当前执行的脚本实例
     */
    @Nonnull
    protected final CosplayScript getScript() {
        return this.script;
    }

    /**
     * 获取当前有效的上下文。
     * <p>
     * 如果在动作中执行，返回动作级别上下文；否则返回脚本级别上下文。
     *
     * @return 当前有效的上下文实例
     */
    @Nonnull
    public final CosplayContext getCurrentContext() {
        CosplayAction currentAction = getCurrentAction();
        if (currentAction == null) {
            return getContextOnScriptScope();
        } else {
            return currentAction.context();
        }
    }

    /**
     * 获取脚本级别的上下文。
     *
     * @return 脚本级别的上下文实例
     */
    @Nonnull
    public final CosplayContext getContextOnScriptScope() {
        Objects.requireNonNull(contextOnScriptScope);
        return contextOnScriptScope;
    }

    /**
     * 获取当前正在执行的动作。
     *
     * @return 当前执行的动作实例，如果不在动作中则返回null
     */
    @Nullable
    protected final CosplayAction getCurrentAction() {
        if (actionStack.isEmpty()) {
            return null;
        } else {
            return actionStack.peek();
        }
    }

    /**
     * 获取问题记录中心。
     *
     * @return 问题记录中心实例
     */
    @Nonnull
    protected final KeelIssueRecordCenter getIssueRecordCenter() {
        return issueRecordCenter;
    }

    private Future<Void> runAfterInitialization() {
        getLogger().info("engine routine start");

        CosplayScene startingScene = script.getStartingScene();
        return startingScene.initialize(this)
                            .compose(v -> {
                                currentSceneRef.set(startingScene);
                                return Keel.asyncCallRepeatedly(repeatedlyCallTask -> {
                                    CosplayScene currentScene = currentSceneRef.get();
                                    if (currentScene == null) {
                                        repeatedlyCallTask.stop();
                                        return Future.succeededFuture();
                                    }

                                    getLogger().info("next scene to run on engine: " + currentScene.getSceneCode());

                                    return currentScene.play()
                                                       .compose(currentScenePlayed -> {
                                                           return seekNextSceneAfterCurrentCosplayScenePlayed();
                                                       });
                                });
                            })
                            .compose(end -> {
                                getLogger().info("engine routine end");
                                return Future.succeededFuture();
                            });
    }

    private Future<Void> seekNextSceneAfterCurrentCosplayScenePlayed() {
        CosplayScene currentScene = currentSceneRef.get();
        CosplayAction currentAction = null;
        if (!actionStack.isEmpty()) {
            currentAction = actionStack.peek();
        }

        if (currentAction != null) {
            String nextSceneInAction = currentAction.seekNextSceneInAction(currentScene.getSceneCode());
            if (nextSceneInAction == null) {
                CosplayAction leftAction = actionStack.pop();
                while (true) {
                    if (actionStack.isEmpty()) {
                        leftAction.output(contextOnScriptScope);
                        String nextSceneInScript = script.seekNextSceneInScript(contextOnScriptScope, currentAction.getSceneCode());
                        return this.confirmNextSceneInScriptScope(nextSceneInScript);
                    } else {
                        currentAction = actionStack.peek();
                        leftAction.output(currentAction.context());

                        String nextSceneInOuterAction = currentAction.seekNextSceneInAction(leftAction.getSceneCode());
                        if (nextSceneInOuterAction == null) {
                            leftAction = actionStack.pop();
                            continue;
                        } else {
                            return this.confirmNextSceneInActionScope(currentAction, nextSceneInOuterAction);
                        }
                    }
                }
            } else {
                return confirmNextSceneInActionScope(currentAction, nextSceneInAction);
            }
        } else {
            String nextSceneInScript = script.seekNextSceneInScript(contextOnScriptScope, currentScene.getSceneCode());
            return this.confirmNextSceneInScriptScope(nextSceneInScript);
        }
    }

    private Future<Void> confirmNextSceneInScriptScope(@Nullable String nextSceneInScript) {
        if (nextSceneInScript == null) {
            currentSceneRef.set(null);
            return Future.succeededFuture();
        }

        CosplayScene sceneByCode = script.getSceneByCodeInScript(nextSceneInScript);
        return confirmNextScene(sceneByCode);
    }

    private Future<Void> confirmNextSceneInActionScope(@Nonnull CosplayAction outerAction, @Nonnull String nextSceneInAction) {
        CosplayScene sceneByCode = outerAction.getSceneByCodeInAction(nextSceneInAction);
        return confirmNextScene(sceneByCode);
    }

    private Future<Void> confirmNextScene(@Nonnull CosplayScene nextScene) {
        if (nextScene instanceof CosplayAction nextAction) {
            // next is action
            CosplayContext outerScopeContext = getCurrentContext();
            return nextAction.initialize(this)
                             .compose(v -> {
                                 actionStack.push(nextAction);
                                 nextAction.input(outerScopeContext);
                                 currentSceneRef.set(nextAction);
                                 return Future.succeededFuture();
                             });
        } else {
            // next is scene
            return nextScene.initialize(this)
                            .compose(v -> {
                                currentSceneRef.set(nextScene);
                                return Future.succeededFuture();
                            });
        }
    }

    @Deprecated(forRemoval = true)
    public Future<Void> startup(@Nonnull Map<String, String> inputMap) {
        return startup(ctx -> {
            for (var entry : inputMap.entrySet()) {
                ctx.writeString(entry.getKey(), entry.getValue());
            }
        });
    }

    /**
     * 启动引擎并开始执行脚本。
     *
     * @return 执行完成的Future
     */
    public Future<Void> startup(@Nonnull Handler<CosplayContext> contextHandler) {
        this.logger = this.generateLogger();
        return this.initialize()
                   .compose(initialized -> {
                       CosplayContext ctx = getCurrentContext();
                       contextHandler.handle(ctx);

                       return runAfterInitialization();
                   })
                   .onFailure(throwable -> {
                       getLogger().exception(throwable, "CosplayEngine Failed");
                   });
    }

    /**
     * 获取引擎的日志记录器。
     *
     * @return 日志记录器实例，用于记录引擎运行时的事件和问题
     * @throws IllegalStateException 如果日志记录器尚未初始化
     */
    @Nonnull
    public KeelIssueRecorder<KeelEventLog> getLogger() throws IllegalStateException {
        if (logger == null) throw new IllegalStateException();
        return logger;
    }

    /**
     * 获取引擎的唯一标识符。
     *
     * @return 引擎的唯一标识符
     */
    @Nonnull
    public final String getEngineId() {
        return engineId;
    }

    /**
     * 生成引擎专用的日志记录器。
     *
     * @return 包含引擎状态信息的日志记录器
     */
    @Nonnull
    public KeelIssueRecorder<KeelEventLog> generateLogger() {
        return getIssueRecordCenter().generateIssueRecorder("CosplayEngine", () -> {
            KeelEventLog log = new KeelEventLog();

            log.context("engine_id", engineId);

            if (!actionStack.isEmpty()) {
                JsonArray array = new JsonArray();
                actionStack.forEach(action -> {
                    array.add(new JsonObject()
                            .put("action_code", action.getSceneCode())
                            .put("instance_id", action.getInstanceId())
                    );
                });
                log.context("actions", array);
            }

            CosplayScene currentScene = currentSceneRef.get();
            if (currentScene != null) {
                log.context("scene", new JsonObject()
                        .put("scene_code", currentScene.getSceneCode())
                        .put("instance_id", currentScene.getInstanceId())
                );
            }

            return log;
        });
    }
}
