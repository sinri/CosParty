package io.github.sinri.CosParty.facade;

import javax.annotation.Nonnull;

/**
 * An instruction set to order AI work as flow, one {@link CosplayScene} by one.
 */
public interface CosplayScript {
    /**
     * @return the {@link CosplayScene} to play in the very beginning.
     */
    @Nonnull
    CosplayScene getStartingScene();

    @Nonnull
    CosplayScene getSceneByCode(@Nonnull String sceneCode);
}
