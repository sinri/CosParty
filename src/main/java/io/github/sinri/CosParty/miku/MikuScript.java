package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayScene;
import io.github.sinri.CosParty.kernel.CosplayScript;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Miku脚本抽象基类。
 * <p>
 * 提供脚本的基本实现，支持场景注册、起始场景设置和动态场景加载。
 * 支持通过包名和类名进行场景管理。
 */
public abstract class MikuScript implements CosplayScript {
    private final @Nonnull Map<String, CosplayScene> sceneMap;
    private @Nonnull String startSceneCode = "";

    public MikuScript() {
        sceneMap = new HashMap<>();
    }

    /**
     * 确认起始场景。
     *
     * @param startSceneClass 起始场景类
     * @return 当前脚本实例
     */
    public MikuScript confirmStartScene(@Nonnull Class<? extends MikuScene> startSceneClass) {
        if (!this.sceneMap.containsKey(startSceneClass.getName())) {
            throw new RuntimeException("This scene code is not registered");
        }
        this.startSceneCode = startSceneClass.getName();
        return this;
    }

    /**
     * 添加场景到脚本中。
     *
     * @param scene 场景实例
     * @return 当前脚本实例
     */
    private MikuScript addScene(@Nonnull CosplayScene scene) {
        sceneMap.put(scene.getSceneCode(), scene);
        return this;
    }

    /**
     * 通过场景类添加场景到脚本中。
     *
     * @param sceneClass 场景类
     * @return 当前脚本实例
     */
    public MikuScript addScene(@Nonnull Class<? extends CosplayScene> sceneClass) {
        if (sceneMap.containsKey(sceneClass.getName())) {
            throw new IllegalArgumentException("already exists");
        }
        CosplayScene scene = loadSceneByCode(sceneClass.getName());
        sceneMap.put(scene.getSceneCode(), scene);
        return this;
    }

    @Nonnull
    @Override
    public CosplayScene getStartingScene() {
        var startingScene = this.sceneMap.get(startSceneCode);
        Objects.requireNonNull(startingScene);
        return startingScene;
    }

    @Nonnull
    @Override
    public CosplayScene getSceneByCodeInScript(@Nonnull String sceneCode) {
        if (!sceneMap.containsKey(sceneCode)) {
            throw new IllegalArgumentException("Scene Code [%s] not found".formatted(sceneCode));
        }
        return sceneMap.get(sceneCode);
    }

    /**
     * 通过场景代码动态加载场景实例。
     *
     * @param sceneCode 场景代码
     * @return 场景实例
     */
    private CosplayScene loadSceneByCode(@Nonnull String sceneCode) {
        String className = sceneCode;

        try {
            Class<?> aClass = Class.forName(className);
            Constructor<?> constructor = aClass.getConstructor();
            Object o = constructor.newInstance();
            if (o instanceof CosplayScene) {
                return (CosplayScene) o;
            }
            throw new RuntimeException("Class is not " + CosplayScene.class.getName());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException("Scene Code [%s] Error: %s".formatted(sceneCode, e.getMessage()), e);
        }
    }
}
