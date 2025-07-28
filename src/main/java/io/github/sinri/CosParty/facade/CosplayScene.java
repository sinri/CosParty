package io.github.sinri.CosParty.facade;

import io.vertx.core.Future;

import javax.annotation.Nonnull;

public interface CosplayScene {
    /**
     * @return the unique identifier of a scene, amongst the scenes in one script.
     */
    @Nonnull
    String getSceneCode();

    /**
     * @param cosplayEngine the {@link CosplayEngine} which play this {@link CosplayScene}, as background and context,
     *                      i.e. all the IO and storage is delegated by engine.
     * @return the future of the code of next scene.
     */
    @Nonnull
    Future<String> play(@Nonnull CosplayEngine cosplayEngine);

}
