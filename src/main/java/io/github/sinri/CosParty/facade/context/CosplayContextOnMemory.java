package io.github.sinri.CosParty.facade.context;

import io.github.sinri.CosParty.facade.context.conversation.ConversationContext;
import io.vertx.core.shareddata.LocalMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * 基于内存的Cosplay上下文实现类
 * <p>
 * 该类实现了CosplayContext接口，使用Vert.x的LocalMap来存储上下文数据，
 * 并提供对话上下文的管理功能。所有数据都存储在内存中，适用于单机部署场景。
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>字符串类型键值对的读写操作</li>
 *   <li>对话上下文的创建和获取</li>
 *   <li>线程安全的对话上下文管理</li>
 * </ul>
 * <p>
 * 该类是线程安全的，使用synchronized关键字保护对话上下文列表的访问。
 */
class CosplayContextOnMemory implements CosplayContext {
    /**
     * 用于存储上下文数据的本地映射
     * <p>
     * 使用Vert.x的LocalMap实现，提供线程安全的数据存储
     */
    private final LocalMap<String, String> contextMap;
    
    /**
     * 对话上下文列表
     * <p>
     * 存储所有创建的对话上下文，通过索引进行访问
     */
    private final List<ConversationContext> conversationContexts;

    /**
     * 构造函数
     * <p>
     * 根据引擎ID初始化上下文存储，创建对应的LocalMap实例
     * 
     * @param engineId 引擎唯一标识符，用于创建对应的LocalMap
     */
    CosplayContextOnMemory(String engineId) {
        contextMap = Keel.getVertx().sharedData().getLocalMap(engineId);
        this.conversationContexts = new ArrayList<>();
    }

    /**
     * 读取字符串类型的上下文数据
     * <p>
     * 根据指定的键从LocalMap中获取对应的字符串值
     * 
     * @param key 要读取的数据键，不能为null
     * @return 对应的字符串值，如果键不存在则返回null
     */
    @Override
    public String readString(@Nonnull String key) {
        return contextMap.get(key);
    }

    /**
     * 写入字符串类型的上下文数据
     * <p>
     * 将键值对存储到LocalMap中，支持链式调用
     * 
     * @param key 数据键，不能为null
     * @param value 要存储的字符串值，不能为null
     * @return 当前上下文实例，支持链式调用
     */
    @Override
    public CosplayContext writeString(@Nonnull String key, @Nonnull String value) {
        contextMap.put(key, value);
        return this;
    }

    /**
     * 根据索引获取对话上下文
     * <p>
     * 通过对话上下文的索引ID获取对应的ConversationContext实例
     * 
     * @param conversationContextId 对话上下文的索引ID
     * @return 对应的对话上下文实例，如果索引无效则可能抛出IndexOutOfBoundsException
     */
    @Override
    public ConversationContext getConversationContext(int conversationContextId) {
        synchronized (conversationContexts) {
            return this.conversationContexts.get(conversationContextId);
        }
    }

    /**
     * 创建新的对话上下文
     * <p>
     * 创建一个新的ConversationContext实例，并将其添加到内部列表中。
     * 新创建的对话上下文会被分配一个唯一的索引ID，该ID等于其在列表中的位置。
     * <p>
     * 此方法是线程安全的，使用synchronized关键字保护列表操作。
     * 
     * @return 新创建的对话上下文实例，已设置正确的索引ID
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
