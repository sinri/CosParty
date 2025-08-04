package io.github.sinri.CosParty.kernel.context;

import javax.annotation.Nonnull;

/**
 * 角色扮演场景的状态和数据管理接口。
 * <p>
 * 提供键值对存储机制，支持字符串、数值、JSON对象等多种数据类型。
 * 所有数据读写操作均为同步接口，持久化需要自行实现。
 * <p>
 * 实现类应确保线程安全，特别是在多线程环境下使用内存存储时。
 *
 * @since 1.0
 */
public interface CosplayContext extends CosplayContextNumberMixin, CosplayContextJsonMixin, CosplayContextConversationMixin {
    /**
     * 创建基于内存存储的角色扮演上下文实例。
     * <p>
     * 适用于临时场景，数据仅在内存中保存，场景结束后自动清理。
     *
     * @return 新的内存角色扮演上下文实例
     */
    static CosplayContext withMemory() {
        return new CosplayContextOnMemory();
    }

    /**
     * 获取上下文的唯一标识符。
     *
     * @return 上下文ID，非空字符串
     */
    @Nonnull
    String getContextId();

    /**
     * 从另一个上下文复制数据到当前上下文。
     * <p>
     * 如果源数据不存在，则不执行任何操作。
     *
     * @param anotherContext            源上下文
     * @param fieldNameInAnotherContext 源上下文中的字段名
     * @param fieldNameInThisContext    当前上下文中的字段名
     */
    default void pullFromAnotherContext(
            @Nonnull CosplayContext anotherContext,
            @Nonnull String fieldNameInAnotherContext,
            @Nonnull String fieldNameInThisContext
    ) {
        String s = anotherContext.readString(fieldNameInAnotherContext);
        if (s != null) {
            this.writeString(fieldNameInThisContext, s);
        }
    }
}
