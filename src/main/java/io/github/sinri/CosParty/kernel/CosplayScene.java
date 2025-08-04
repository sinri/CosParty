package io.github.sinri.CosParty.kernel;

import io.vertx.core.Future;

import javax.annotation.Nonnull;

/**
 * 角色扮演场景接口。
 * <p>
 * 场景是角色扮演系统的最小执行单元，代表一个具体的业务逻辑片段。
 * 每个场景都有唯一的场景代码和实例ID，用于标识和追踪。
 */
public interface CosplayScene {
    /**
     * 获取场景的唯一标识代码。
     *
     * @return 场景的唯一标识代码
     */
    @Nonnull
    String getSceneCode();

    /**
     * 获取场景的实例ID。
     *
     * @return 场景实例的唯一ID
     */
    @Nonnull
    String getInstanceId();

    /**
     * 初始化场景。
     *
     * @param engine 执行引擎实例
     * @return 初始化完成的Future
     */
    Future<Void> initialize(@Nonnull CosplayEngine engine);

    /**
     * 执行场景的核心业务逻辑。
     *
     * @param engine 执行引擎实例
     * @return 执行完成的Future
     */
    @Nonnull
    Future<Void> play(@Nonnull CosplayEngine engine);

}
