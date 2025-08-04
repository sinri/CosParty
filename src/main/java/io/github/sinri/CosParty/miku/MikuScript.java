package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayScene;
import io.github.sinri.CosParty.kernel.CosplayScript;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * Miku脚本抽象基类。
 * <p>
 * 提供脚本的基本实现，支持场景注册、起始场景设置和动态场景加载。
 * 支持通过包名和类名进行场景管理。
 */
public abstract class MikuScript implements CosplayScript {
    private final @Nonnull Set<String> sceneCodeSet;
    private @Nonnull String startSceneCode = "";

    public MikuScript() {
        sceneCodeSet = new HashSet<>();
    }


    /**
     * 确认起始场景。
     *
     * @param startSceneClass 起始场景类
     * @return 当前脚本实例
     */
    public MikuScript confirmStartScene(@Nonnull Class<? extends MikuScene> startSceneClass) {
        if (!this.sceneCodeSet.contains(startSceneClass.getName())) {
            throw new RuntimeException("This scene code is not registered");
        }
        this.startSceneCode = startSceneClass.getName();
        return this;
    }

    /**
     * 通过场景类添加场景到脚本中。
     *
     * @param sceneClass 场景类
     * @return 当前脚本实例
     */
    public MikuScript addScene(@Nonnull Class<? extends CosplayScene> sceneClass) {
        if (sceneCodeSet.contains(sceneClass.getName())) {
            throw new IllegalArgumentException("already exists");
        }
        sceneCodeSet.add(sceneClass.getName());
        return this;
    }

    @Nonnull
    @Override
    public CosplayScene getStartingScene() {
        return this.getSceneByCodeInScript(startSceneCode);
    }

    @Nonnull
    @Override
    public CosplayScene getSceneByCodeInScript(@Nonnull String sceneCode) {
        if (!sceneCodeSet.contains(sceneCode)) {
            throw new IllegalArgumentException("Scene Code [%s] not found".formatted(sceneCode));
        }
        return MikuEngine.loadSceneByCode(sceneCode, CosplayScene.class);
    }
}
