package io.github.sinri.CosParty.kernel.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CosplayContextCore {
    /**
     * 读取字符串值。
     * <p>
     * 如果键不存在或值为null，返回null。
     *
     * @param key 数据键
     * @return 对应的字符串值，可能为null
     */
    @Nullable
    String readString(@Nonnull String key);

    /**
     * 写入字符串值。
     * <p>
     * 将字符串值与指定键关联存储。
     *
     * @param key   数据键
     * @param value 字符串值
     * @return 当前上下文实例，支持链式调用
     */
    void writeString(@Nonnull String key, @Nonnull String value);

}
