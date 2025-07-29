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
 * 角色扮演引擎，用于按照 {@link CosplayScript} 定义的顺序运行AI工作流。
 * <p>
 * 该引擎负责管理场景的切换和执行，维护上下文状态，并处理整个角色扮演流程的生命周期。
 * 每个引擎实例都有唯一的ID，可以独立运行不同的脚本。
 * <p>
 * 使用方式：
 * <ul>
 *   <li>继承此类并实现 {@link #initialize()} 方法</li>
 *   <li>调用 {@link #swift(Map)} 方法开始执行</li>
 * </ul>
 * 
 * @author sinri
 * @since 1.0.0
 */
public abstract class CosplayEngine {
    /**
     * 引擎的唯一标识符，格式为：类名@UUID
     */
    private final String engineId;
    
    /**
     * 角色扮演脚本，定义了场景的执行顺序和逻辑
     */
    @Nonnull
    private final CosplayScript cosplayScript;
    
    /**
     * 角色扮演上下文，存储执行过程中的状态和数据
     */
    protected CosplayContext cosplayContext;
    
    /**
     * 问题记录中心，用于记录和输出执行过程中的日志和错误信息。
     * <p>
     * 默认初始化为 {@link io.github.sinri.keel.logger.issue.center.KeelIssueRecordCenter#outputCenter()}。
     * 如需自定义，请在 {@link #initialize()} 方法中重写。
     */
    @Nonnull
    protected KeelIssueRecordCenter issueRecordCenter;

    /**
     * 构造函数，创建角色扮演引擎实例。
     * <p>
     * 会自动生成唯一的引擎ID，并初始化问题记录中心。
     * 
     * @param cosplayScript 角色扮演脚本，不能为null
     * @throws IllegalArgumentException 如果cosplayScript为null
     */
    public CosplayEngine(@Nonnull CosplayScript cosplayScript) {
        this.engineId = getClass().getName() + "@" + UUID.randomUUID();
        this.cosplayScript = cosplayScript;
        this.issueRecordCenter = KeelIssueRecordCenter.outputCenter();
    }

    /**
     * 初始化引擎执行所需的各种资源。
     * <p>
     * 子类必须实现此方法，用于：
     * <ul>
     *   <li>初始化上下文对象</li>
     *   <li>设置问题记录中心</li>
     *   <li>准备其他必要的资源</li>
     * </ul>
     * 
     * @return 初始化完成的Future
     */
    abstract protected Future<Void> initialize();

    /**
     * 获取角色扮演脚本。
     * 
     * @return 当前引擎使用的脚本对象
     */
    @Nonnull
    protected final CosplayScript getCosplayScript() {
        return this.cosplayScript;
    }

    /**
     * 获取角色扮演上下文。
     * <p>
     * 上下文必须在 {@link #initialize()} 方法中被正确初始化。
     * 
     * @return 当前引擎的上下文对象
     * @throws NullPointerException 如果上下文尚未初始化
     */
    @Nonnull
    public final CosplayContext getCosplayContext() {
        Objects.requireNonNull(cosplayContext);
        return cosplayContext;
    }

    /**
     * 获取问题记录中心。
     * 
     * @return 当前引擎使用的问题记录中心
     */
    @Nonnull
    public final KeelIssueRecordCenter getIssueRecordCenter() {
        return issueRecordCenter;
    }

    /**
     * 在初始化完成后运行场景流程。
     * <p>
     * 该方法会：
     * <ol>
     *   <li>获取起始场景</li>
     *   <li>循环执行场景，直到没有下一个场景</li>
     *   <li>根据场景返回的代码切换到下一个场景</li>
     * </ol>
     * 
     * @return 执行完成的Future
     */
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
     * 快速执行模式，不使用verticle托管，直接初始化并执行。
     * <p>
     * 适用于较短的任务，执行流程：
     * <ol>
     *   <li>调用 {@link #initialize()} 进行初始化</li>
     *   <li>将输入参数写入上下文</li>
     *   <li>执行场景流程</li>
     * </ol>
     * 
     * @param inputMap 输入参数映射，键值对会被写入上下文
     * @return 执行完成的Future
     * @throws IllegalArgumentException 如果inputMap为null
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

    /**
     * 获取引擎的唯一标识符。
     * 
     * @return 引擎ID字符串
     */
    public final String getEngineId() {
        return engineId;
    }
}
