package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.facade.CosplayScene;
import io.github.sinri.CosParty.facade.CosplayScript;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MikuScript implements CosplayScript {
    private final @Nonnull Map<String, MikuScene> sceneMap;
    private final @Nonnull String startSceneCode;

    public MikuScript(@Nonnull MikuScene startScene) {
        sceneMap = new HashMap<>();
        sceneMap.put(startScene.getSceneCode(), startScene);
        this.startSceneCode = startScene.getSceneCode();
    }

    public MikuScript addScene(@Nonnull MikuScene scene) {
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
    public CosplayScene getSceneByCode(@Nonnull String sceneCode) {
        var scene = this.sceneMap.get(sceneCode);
        Objects.requireNonNull(scene);
        return scene;
    }
}
