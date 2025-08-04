package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayEngine;
import io.github.sinri.CosParty.kernel.CosplayScene;
import io.github.sinri.CosParty.kernel.CosplayScript;
import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Miku引擎实现。
 * <p>
 * 提供基于内存的上下文管理，适用于简单的角色扮演场景。
 * <p>
 * 在Miku实现中，一个场景类的唯一标识为其类的唯一名称，且其仅在运行到该场景时才会被实例化加载并初始化运行。
 * 其他时候，通过场景代码获取的场景实例，并非该场景在引擎中运行的实例。
 */
public final class MikuEngine extends CosplayEngine {

    /**
     * 构造Miku引擎实例。
     *
     * @param cosplayScript 要执行的脚本
     */
    public MikuEngine(@Nonnull CosplayScript cosplayScript) {
        super(cosplayScript);
    }

    @Override
    protected Future<Void> initialize() {
        this.contextOnScriptScope = CosplayContext.withMemory();
        return Future.succeededFuture();
    }

    /**
     * 通过场景代码动态加载场景实例。
     *
     * @param sceneCode 场景代码
     * @return 场景实例
     */
    public static <T extends CosplayScene> T loadSceneByCode(@Nonnull String sceneCode, Class<T> tClass) {
        String className = sceneCode;

        try {
            Class<?> aClass = Class.forName(className);
            Constructor<?> constructor = aClass.getConstructor();
            Object o = constructor.newInstance();
            if (tClass.isInstance(o)) {
                return tClass.cast(o);
            }
            throw new RuntimeException("Class is not " + CosplayScene.class.getName());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException("Scene Code [%s] Error: %s".formatted(sceneCode, e.getMessage()), e);
        }
    }
}
