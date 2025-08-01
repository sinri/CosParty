package io.github.sinri.CosParty.kernel;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.keel.logger.event.KeelEventLog;
import io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter;
import io.github.sinri.keel.logger.issue.recorder.KeelIssueRecorder;
import io.vertx.core.Future;
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
 * 角色扮演引擎抽象基类，负责执行角色扮演脚本并管理场景流转。
 * <p>
 * CosplayEngine 是角色扮演系统的核心执行引擎，负责协调脚本（CosplayScript）、
 * 场景（CosplayScene）和上下文（CosplayContext）之间的交互。引擎通过以下机制
 * 实现复杂的场景流转：
 * <ul>
 *   <li>场景栈管理：使用动作栈（actionStack）管理嵌套的动作场景</li>
 *   <li>上下文传递：在脚本级别和动作级别之间传递上下文数据</li>
 *   <li>场景跳转：根据当前场景执行结果确定下一个要执行的场景</li>
 *   <li>生命周期管理：管理场景的初始化、执行和清理过程</li>
 * </ul>
 * <p>
 * 引擎的执行流程如下：
 * <ol>
 *   <li>初始化：调用 {@link #initialize()} 进行引擎级别的初始化</li>
 *   <li>启动：通过 {@link #startup(Map)} 启动脚本执行</li>
 *   <li>场景循环：重复执行当前场景，并根据结果跳转到下一个场景</li>
 *   <li>结束：当脚本执行完毕或遇到结束条件时停止执行</li>
 * </ol>
 * <p>
 * 子类需要实现 {@link #initialize()} 方法来提供具体的初始化逻辑，
 * 通常包括创建脚本级别的上下文（contextOnScriptScope）。
 * <p>
 * 引擎提供了丰富的上下文访问方法，场景可以通过 {@link #getCurrentContext()}
 * 获取当前有效的上下文，用于数据读写和状态管理。
 *
 * @see CosplayScript
 * @see CosplayScene
 * @see CosplayAction
 * @see CosplayContext
 * @since 1.0
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

    /**
     * 构造角色扮演引擎实例。
     * <p>
     * 创建引擎时会生成唯一的引擎ID，用于日志记录和调试追踪。
     * 引擎ID由类名和随机UUID组成，确保每个引擎实例的唯一性。
     * <p>
     * 此构造函数会初始化问题记录中心，用于后续的日志记录和错误追踪。
     * 子类应在 {@link #initialize()} 方法中完成具体的初始化工作。
     *
     * @param script 要执行的角色扮演脚本，不能为null
     * @throws IllegalArgumentException 当script为null时抛出
     */
    public CosplayEngine(@Nonnull CosplayScript script) {
        this.engineId = getClass().getName() + "@" + UUID.randomUUID();
        this.script = script;
        this.issueRecordCenter = KeelIssueRecordCenter.outputCenter();
        this.actionStack = new Stack<>();
    }

    /**
     * 初始化引擎。
     * <p>
     * 子类必须实现此方法来提供具体的初始化逻辑。初始化阶段通常包括：
     * <ul>
     *   <li>创建脚本级别的上下文（contextOnScriptScope）</li>
     *   <li>设置引擎的初始状态</li>
     *   <li>准备执行所需的资源</li>
     *   <li>验证脚本的有效性</li>
     * </ul>
     * <p>
     * 此方法在 {@link #startup(Map)} 中被调用，是引擎启动流程的第一步。
     * 如果初始化失败，引擎将无法正常启动。
     * <p>
     * 典型的实现示例：
     * <pre>{@code
     * @Override
     * protected Future<Void> initialize() {
     *     this.contextOnScriptScope = CosplayContext.withMemory();
     *     return Future.succeededFuture();
     * }
     * }</pre>
     *
     * @return 初始化完成的Future，成功时返回succeededFuture，失败时返回failedFuture
     */
    abstract protected Future<Void> initialize();

    /**
     * 获取当前引擎执行的脚本。
     * <p>
     * 返回引擎构造时传入的脚本实例，用于访问脚本中定义的场景和跳转逻辑。
     * 此方法主要在引擎内部使用，用于场景管理和跳转决策。
     *
     * @return 当前执行的脚本实例，不能为null
     */
    @Nonnull
    protected final CosplayScript getScript() {
        return this.script;
    }

    /**
     * 获取当前有效的上下文。
     * <p>
     * 根据当前执行状态返回相应的上下文：
     * <ul>
     *   <li>如果当前在动作（CosplayAction）中执行，返回动作级别的上下文</li>
     *   <li>如果当前在脚本级别执行，返回脚本级别的上下文</li>
     * </ul>
     * <p>
     * 此方法是场景获取上下文的主要入口，场景可以通过此方法读取和修改
     * 当前作用域内的数据。上下文的选择逻辑确保了数据作用域的正确性。
     * <p>
     * 此方法在以下场景中被调用：
     * <ul>
     *   <li>场景执行时获取当前上下文</li>
     *   <li>动作输入输出时确定上下文作用域</li>
     *   <li>脚本启动时设置初始参数</li>
     * </ul>
     *
     * @return 当前有效的上下文实例，不能为null
     * @throws IllegalStateException 当脚本级别上下文未初始化时抛出
     */
    @Nonnull
    public final CosplayContext getCurrentContext() {
        CosplayAction currentAction = getCurrentAction();
        if (currentAction == null) {
            return getContextOnScriptScope();
        } else {
            return currentAction.getContextOnThisActionScope();
        }
    }

    /**
     * 获取脚本级别的上下文。
     * <p>
     * 返回引擎维护的脚本级别上下文，包含整个脚本执行过程中的全局状态数据。
     * 脚本级别上下文在引擎初始化时创建，在脚本执行期间持续存在。
     * <p>
     * 此方法主要用于：
     * <ul>
     *   <li>动作（CosplayAction）输入输出时访问脚本级别数据</li>
     *   <li>脚本启动时设置初始参数</li>
     *   <li>脚本级别场景访问全局状态</li>
     * </ul>
     * <p>
     * 如果脚本级别上下文未初始化，此方法会抛出异常。
     *
     * @return 脚本级别的上下文实例，不能为null
     * @throws NullPointerException 当contextOnScriptScope为null时抛出
     */
    @Nonnull
    public final CosplayContext getContextOnScriptScope() {
        Objects.requireNonNull(contextOnScriptScope);
        return contextOnScriptScope;
    }

    /**
     * 获取当前正在执行的动作。
     * <p>
     * 返回动作栈顶部的动作实例，如果当前不在任何动作中执行则返回null。
     * 动作栈用于管理嵌套的动作场景，支持多层嵌套的场景结构。
     * <p>
     * 此方法主要用于：
     * <ul>
     *   <li>确定当前上下文的作用域</li>
     *   <li>动作跳转时查找外层动作</li>
     *   <li>日志记录时标识当前执行状态</li>
     * </ul>
     * <p>
     * 动作栈的管理由引擎自动处理，场景无需手动管理栈操作。
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
     * <p>
     * 返回引擎使用的问题记录中心实例，用于生成日志记录器。
     * 问题记录中心负责管理日志记录和错误追踪功能。
     * <p>
     * 此方法主要用于子类或场景生成日志记录器，记录执行过程中的
     * 重要事件和错误信息。
     *
     * @return 问题记录中心实例，不能为null
     */
    @Nonnull
    protected final KeelIssueRecordCenter getIssueRecordCenter() {
        return issueRecordCenter;
    }

    private Future<Void> runAfterInitialization() {
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
                                    return currentScene.play(this)
                                                       .compose(currentScenePlayed -> {
                                                           return seekNextSceneAfterCurrentCosplayScenePlayed();
                                                       });
                                });
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
                        leftAction.output(currentAction.getContextOnThisActionScope());

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

    /**
     * 启动引擎并开始执行脚本。
     * <p>
     * 这是引擎的主要入口方法，负责启动整个角色扮演脚本的执行流程。
     * 启动过程包括以下步骤：
     * <ol>
     *   <li>调用 {@link #initialize()} 进行引擎初始化</li>
     *   <li>将输入参数写入脚本级别上下文</li>
     *   <li>获取起始场景并开始执行循环</li>
     *   <li>根据场景执行结果进行场景跳转</li>
     *   <li>当脚本执行完毕时结束</li>
     * </ol>
     * <p>
     * 输入参数会被写入脚本级别上下文，场景可以通过上下文访问这些参数。
     * 引擎会自动处理场景的初始化、执行和跳转逻辑，直到脚本执行完毕。
     * <p>
     * 如果执行过程中发生异常，引擎会记录错误日志并返回失败的Future。
     * 调用方可以通过Future的异常处理机制来捕获和处理错误。
     * <p>
     * 此方法在以下场景中被调用：
     * <ul>
     *   <li>应用程序启动角色扮演流程时</li>
     *   <li>测试用例验证脚本执行时</li>
     *   <li>用户触发角色扮演功能时</li>
     * </ul>
     *
     * @param inputMap 初始输入参数，键值对形式，不能为null
     * @return 执行完成的Future，成功时返回succeededFuture，失败时返回failedFuture
     * @throws IllegalArgumentException 当inputMap为null时抛出
     */
    public Future<Void> startup(@Nonnull Map<String, String> inputMap) {
        KeelIssueRecorder<KeelEventLog> logger = this.generateLogger();
        return this.initialize()
                   .compose(initialized -> {
                       CosplayContext ctx = getCurrentContext();
                       for (var entry : inputMap.entrySet()) {
                           ctx.writeString(entry.getKey(), entry.getValue());
                       }

                       return runAfterInitialization();
                   })
                   .onFailure(throwable -> {
                       logger.exception(throwable, "CosplayEngine Failed");
                   });
    }

    /**
     * 获取引擎的唯一标识符。
     * <p>
     * 返回引擎的唯一ID，由类名和随机UUID组成。引擎ID用于：
     * <ul>
     *   <li>日志记录时标识具体的引擎实例</li>
     *   <li>调试时追踪引擎的执行状态</li>
     *   <li>多引擎环境下的实例区分</li>
     * </ul>
     * <p>
     * 每个引擎实例都有唯一的ID，即使同一类的多个实例也能被区分。
     *
     * @return 引擎的唯一标识符，不能为null
     */
    @Nonnull
    public final String getEngineId() {
        return engineId;
    }

    /**
     * 生成引擎专用的日志记录器。
     * <p>
     * 创建一个包含引擎状态信息的日志记录器，用于记录引擎执行过程中的
     * 重要事件和调试信息。日志记录器会自动包含以下上下文信息：
     * <ul>
     *   <li>引擎ID：用于标识具体的引擎实例</li>
     *   <li>动作栈：当前嵌套的动作列表</li>
     *   <li>当前场景：正在执行的场景信息</li>
     * </ul>
     * <p>
     * 此方法主要用于：
     * <ul>
     *   <li>引擎内部记录执行状态</li>
     *   <li>场景获取日志记录器</li>
     *   <li>错误追踪和调试</li>
     * </ul>
     * <p>
     * 生成的日志记录器是线程安全的，可以在多线程环境中使用。
     *
     * @return 包含引擎状态信息的日志记录器，不能为null
     */
    @Nonnull
    public final KeelIssueRecorder<KeelEventLog> generateLogger() {
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
