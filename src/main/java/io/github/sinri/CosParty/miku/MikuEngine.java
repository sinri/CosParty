package io.github.sinri.CosParty.miku;

import io.github.sinri.CosParty.kernel.CosplayEngine;
import io.github.sinri.CosParty.kernel.CosplayScript;
import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.vertx.core.Future;

import javax.annotation.Nonnull;

/**
 * Miku引擎实现。
 * <p>
 * 提供基于内存的上下文管理，适用于简单的角色扮演场景。
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

}
