package io.github.sinri.CosParty.kernel.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.function.Function;

public interface CosplayContextNumberMixin extends CosplayContextCore {
    /**
     * 读取长整型值。
     * <p>
     * 将字符串值转换为Long类型，转换失败时返回null。
     *
     * @param key 数据键
     * @return 长整型值，转换失败时返回null
     */
    @Nullable
    default Long readLong(@Nonnull String key) {
        try {
            var s = readString(key);
            if (s == null) return null;
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    default long readLong(@Nonnull String key, long fallback) {
        var x = readLong(key);
        return x == null ? fallback : x;
    }

    @Nullable
    default Integer readInteger(@Nonnull String key) {
        Long l = readLong(key);
        if (l == null) return null;
        return Math.toIntExact(l);
    }

    default int readInteger(@Nonnull String key, int fallback) {
        Long l = readLong(key);
        if (l == null) return fallback;
        try {
            return Math.toIntExact(l);
        } catch (ArithmeticException e) {
            return fallback;
        }
    }

    /**
     * 读取双精度浮点值。
     * <p>
     * 将字符串值转换为Double类型，转换失败时返回null。
     *
     * @param key 数据键
     * @return 双精度浮点值，转换失败时返回null
     */
    @Nullable
    default Double readDouble(@Nonnull String key) {
        try {
            var s = readString(key);
            if (s == null) return null;
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    default double readDouble(@Nonnull String key, double fallback) {
        var x = readDouble(key);
        return x == null ? fallback : x;
    }

    /**
     * 读取BigDecimal值。
     * <p>
     * 将字符串值转换为BigDecimal类型，适用于精确的十进制运算。
     *
     * @param key 数据键
     * @return BigDecimal值，转换失败时返回null
     */
    @Nullable
    default BigDecimal readBigDecimal(@Nonnull String key) {
        try {
            var s = readString(key);
            if (s == null) return null;
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nonnull
    default BigDecimal readBigDecimal(@Nonnull String key, @Nonnull BigDecimal fallback) {
        var x = readBigDecimal(key);
        return x == null ? fallback : x;
    }

    /**
     * 计算并更新长整型值。
     * <p>
     * 该方法首先读取指定键的当前长整型值（如果不存在则使用0作为默认值），然后应用给定的函数
     * 对其进行计算，最后将计算结果写回相同的键。
     *
     * @param key    数据键
     * @param lambda 应用于当前值的函数
     * @return 计算后的新值
     */
    default long computedLong(@Nonnull String key, @Nonnull Function<Long, Long> lambda) {
        synchronized (this) {
            long x = lambda.apply(readLong(key, 0));
            writeNumber(key, x);
            return x;
        }
    }


    /**
     * 写入数值。
     * <p>
     * 将数值转换为字符串后存储。
     *
     * @param key   数据键
     * @param value 数值
     */
    default void writeNumber(@Nonnull String key, @Nonnull Number value) {
        writeString(key, value.toString());
    }
}
