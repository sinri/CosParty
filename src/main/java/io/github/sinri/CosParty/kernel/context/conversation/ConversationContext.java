package io.github.sinri.CosParty.kernel.context.conversation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 对话上下文管理类。
 * <p>
 * 管理对话中的角色和对话，提供注册和检索功能。
 */
public class ConversationContext {
    @Nonnull
    private final Map<String, Actor> actorMap;
    @Nonnull
    private final Map<String, Conversation> conversationMap;
    private int conversationContextIndex = -1;

    public ConversationContext() {
        actorMap = new TreeMap<>();
        conversationMap = new HashMap<>();
    }

    /**
     * 获取对话上下文索引。
     *
     * @return 上下文索引，未设置时为-1
     */
    public int getConversationContextIndex() {
        return conversationContextIndex;
    }

    /**
     * 设置对话上下文索引。
     * <p>
     * 只能设置一次。
     *
     * @param conversationContextIndex 要设置的索引
     * @return 当前上下文实例
     */
    @Nonnull
    public ConversationContext setConversationContextIndex(int conversationContextIndex) {
        if (this.conversationContextIndex >= 0) throw new IllegalStateException();
        this.conversationContextIndex = conversationContextIndex;
        return this;
    }

    /**
     * 获取所有注册的角色。
     *
     * @return 所有角色列表
     */
    @Nonnull
    public List<Actor> getActors() {
        return actorMap.values().stream().toList();
    }

    /**
     * 根据名称获取角色。
     *
     * @param actorName 角色名称
     * @return 角色实例，未找到时返回null
     */
    @Nullable
    public Actor getActor(String actorName) {
        return actorMap.get(actorName);
    }

    /**
     * 根据代码获取对话。
     *
     * @param conversationCode 对话代码
     * @return 对话实例，未找到时返回null
     */
    @Nullable
    public Conversation getConversation(String conversationCode) {
        return conversationMap.get(conversationCode);
    }

    /**
     * 注册角色到上下文中。
     *
     * @param actor 要注册的角色
     * @return 当前上下文实例
     */
    @Nonnull
    public ConversationContext registerActor(@Nonnull DynamicActor actor) {
        this.actorMap.put(actor.getActorName(), actor);
        return this;
    }

    /**
     * 注册对话到上下文中。
     *
     * @param conversation 要注册的对话
     * @return 当前上下文实例
     */
    @Nonnull
    public ConversationContext registerConversation(@Nonnull Conversation conversation) {
        this.conversationMap.put(conversation.getConversationCode(), conversation);
        return this;
    }
}
