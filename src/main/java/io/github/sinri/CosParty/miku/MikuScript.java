package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayScene;
import io.github.sinri.CosParty.kernel.CosplayScript;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class MikuScript implements CosplayScript {
    private final @Nonnull Map<String, CosplayScene> sceneMap;
    private @Nonnull String startSceneCode = "";

    @Nonnull
    private String scenePackage = "";

    public MikuScript() {
        sceneMap = new HashMap<>();
    }

    public MikuScript setScenePackage(String scenePackage) {
        this.scenePackage = scenePackage;
        return this;
    }

    public MikuScript confirmStartScene(@Nonnull String startSceneCode) {
        if (!this.sceneMap.containsKey(startSceneCode)) {
            throw new RuntimeException("This scene code is not registered");
        }
        this.startSceneCode = startSceneCode;
        return this;
    }

    public MikuScript confirmStartScene(@Nonnull Class<? extends MikuScene> startSceneClass) {
        return this.confirmStartScene(startSceneClass.getName());
    }

    public MikuScript addScene(@Nonnull CosplayScene scene) {
        sceneMap.put(scene.getSceneCode(), scene);
        return this;
    }

    public MikuScript addScene(@Nonnull Class<? extends CosplayScene> sceneClass) {
        CosplayScene scene = loadSceneByCode(sceneClass.getName());
        return addScene(scene);
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

    private CosplayScene loadSceneByCode(@Nonnull String sceneCode) {
        String className;
        if (!scenePackage.isBlank() && !scenePackage.endsWith(".")) {
            className = scenePackage + "." + sceneCode;
        } else {
            className = scenePackage + sceneCode;
        }

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
