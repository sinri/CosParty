package io.github.sinri.CosParty.kernel.context;

import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的{@link CosplayContext}实现。
 * <p>
 * 使用ConcurrentHashMap存储键值对数据，ArrayList存储对话上下文。
 * 对话上下文操作使用synchronized保证线程安全。
 *
 * @since 1.0
 */
class CosplayContextOnMemory implements CosplayContext {
    /**
     * 存储上下文数据的线程安全映射
     */
    private final Map<String, String> contextMap;

    /**
     * 对话上下文列表，使用synchronized保护访问
     */
    private final List<ConversationContext> conversationContexts;

    /**
     * 上下文唯一标识符
     */
    private final String contextId;

    /**
     * 创建新的内存上下文实例。
     * <p>
     * 生成随机UUID作为上下文ID，初始化线程安全的数据存储。
     */
    CosplayContextOnMemory() {
        this.contextId = UUID.randomUUID().toString();
        this.contextMap = new ConcurrentHashMap<>();
        this.conversationContexts = new ArrayList<>();
    }

    @Nonnull
    @Override
    public String getContextId() {
        return contextId;
    }

    /**
     * 从内存映射中读取字符串值。
     * <p>
     * 如果键不存在，返回null。
     *
     * @param key 数据键，不能为null
     * @return 对应的字符串值，可能为null
     */
    @Override
    public String readString(@Nonnull String key) {
        return contextMap.get(key);
    }

    /**
     * 将字符串值存储到内存映射中。
     * <p>
     * 支持链式调用。
     *
     * @param key   数据键，不能为null
     * @param value 字符串值，不能为null
     */
    @Override
    public void writeString(@Nonnull String key, @Nonnull String value) {
        contextMap.put(key, value);
    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(contextMap.keySet());
    }

    @Nonnull
    @Override
    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(contextMap);
    }

    /**
     * 根据索引获取对话上下文。
     * <p>
     * 如果索引无效，抛出IndexOutOfBoundsException。
     * 此方法是线程安全的。
     *
     * @param conversationContextId 对话上下文索引
     * @return 对应的对话上下文实例
     * @throws IndexOutOfBoundsException 当索引无效时
     */
    @Override
    public ConversationContext getConversationContext(int conversationContextId) {
        synchronized (conversationContexts) {
            return this.conversationContexts.get(conversationContextId);
        }
    }

    /**
     * 创建新的对话上下文。
     * <p>
     * 创建ConversationContext实例并添加到内部列表。
     * 新对话上下文的索引ID等于其在列表中的位置。
     * 此方法是线程安全的。
     *
     * @return 新创建的对话上下文实例
     */
    @Override
    public ConversationContext createConversationContext() {
        synchronized (conversationContexts) {
            ConversationContext conversationContext = new ConversationContext();
            this.conversationContexts.add(conversationContext);
            var id = this.conversationContexts.size() - 1;
            conversationContext.setConversationContextIndex(id);
            return conversationContext;
        }
    }

}
