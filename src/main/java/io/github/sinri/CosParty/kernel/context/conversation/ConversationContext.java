package io.github.sinri.CosParty.kernel.context.conversation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 对话上下文管理器，负责管理同一群角色进行的若干场对话。
 *
 * <p>本类提供以下核心功能：
 * <ul>
 *   <li>角色管理：注册和获取参与对话的角色</li>
 *   <li>对话管理：注册和获取具体的对话实例</li>
 *   <li>上下文索引：为对话上下文分配唯一标识</li>
 * </ul>
 *
 * <p>使用TreeMap存储角色以确保角色名称的有序性，使用HashMap存储对话以提高查找效率。
 * 对话上下文索引只能设置一次，用于标识该上下文在整体对话流程中的位置。
 *
 * @author Sinri
 * @since 1.0
 */
public class ConversationContext {
    @Nonnull
    private final Map<String, Actor> actorMap;
    @Nonnull
    private final Map<String, Conversation> conversationMap;
    private int conversationContextIndex = -1;

    /**
     * 创建新的对话上下文实例。
     *
     * <p>初始化角色映射表（使用TreeMap保证有序性）和对话映射表（使用HashMap提高查找效率）。
     * 对话上下文索引初始化为-1，表示尚未分配索引。
     */
    public ConversationContext() {
        actorMap = new TreeMap<>();
        conversationMap = new HashMap<>();
    }

    /**
     * 获取当前对话上下文的索引值。
     *
     * @return 对话上下文索引，如果未设置则返回-1
     */
    public int getConversationContextIndex() {
        return conversationContextIndex;
    }

    /**
     * 设置对话上下文索引。
     *
     * <p>索引只能设置一次，如果已经设置过索引则抛出IllegalStateException异常。
     * 索引用于标识该对话上下文在整体对话流程中的位置。
     *
     * @param conversationContextIndex 要设置的索引值，必须为非负整数
     * @return 当前实例，支持链式调用
     * @throws IllegalStateException 如果索引已经被设置过
     */
    @Nonnull
    public ConversationContext setConversationContextIndex(int conversationContextIndex) {
        if (this.conversationContextIndex >= 0) throw new IllegalStateException();
        this.conversationContextIndex = conversationContextIndex;
        return this;
    }

    /**
     * 获取所有已注册的角色列表。
     *
     * @return 所有角色的不可修改列表
     */
    @Nonnull
    public List<Actor> getActors() {
        return actorMap.values().stream().toList();
    }

    /**
     * 根据角色名称获取指定的角色。
     *
     * @param actorName 角色名称
     * @return 对应的角色实例，如果不存在则返回null
     */
    @Nullable
    public Actor getActor(String actorName) {
        return actorMap.get(actorName);
    }

    /**
     * 根据对话代码获取指定的对话。
     *
     * @param conversationCode 对话代码
     * @return 对应的对话实例，如果不存在则返回null
     */
    @Nullable
    public Conversation getConversation(String conversationCode) {
        return conversationMap.get(conversationCode);
    }

    /**
     * 注册一个新的角色到当前对话上下文。
     *
     * <p>如果角色名称已存在，则会覆盖原有的角色。
     *
     * @param actor 要注册的角色实例
     * @return 当前实例，支持链式调用
     */
    @Nonnull
    public ConversationContext registerActor(@Nonnull Actor actor) {
        this.actorMap.put(actor.getActorName(), actor);
        return this;
    }

    /**
     * 注册一个新的对话到当前对话上下文。
     *
     * <p>如果对话代码已存在，则会覆盖原有的对话。
     *
     * @param conversation 要注册的对话实例
     * @return 当前实例，支持链式调用
     */
    @Nonnull
    public ConversationContext registerConversation(@Nonnull Conversation conversation) {
        this.conversationMap.put(conversation.getConversationCode(), conversation);
        return this;
    }
}
