package io.github.sinri.CosParty.kernel.context;

import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;

public interface CosplayContextConversationMixin {
    /**
     * 获取指定ID的对话上下文。
     * <p>
     * 如果ID不存在，具体行为由实现类决定。
     *
     * @param conversationContextId 对话上下文ID
     * @return 对应的对话上下文
     */
    ConversationContext getConversationContext(int conversationContextId);

    /**
     * 创建新的对话上下文。
     * <p>
     * 生成具有唯一ID的对话上下文实例。
     *
     * @return 新创建的对话上下文
     */
    ConversationContext createConversationContext();
}
