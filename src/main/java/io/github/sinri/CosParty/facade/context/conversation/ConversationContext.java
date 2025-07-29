package io.github.sinri.CosParty.facade.context.conversation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 本类列管同一群角色进行的若干场对话。
 */
public class ConversationContext {
    private final Map<String, Actor> actorMap;
    private final Map<String, Conversation> conversationMap;
    private int conversationContextIndex = -1;

    public ConversationContext() {
        actorMap = new TreeMap<>();
        conversationMap = new HashMap<>();
    }

    public int getConversationContextIndex() {
        return conversationContextIndex;
    }

    public ConversationContext setConversationContextIndex(int conversationContextIndex) {
        if (this.conversationContextIndex >= 0) throw new IllegalStateException();
        this.conversationContextIndex = conversationContextIndex;
        return this;
    }

    public List<Actor> getActors() {
        return actorMap.values().stream().toList();
    }

    public Actor getActor(String actorName) {
        return actorMap.get(actorName);
    }

    public Conversation getConversation(String conversationCode) {
        return conversationMap.get(conversationCode);
    }

    public ConversationContext registerActor(Actor actor) {
        this.actorMap.put(actor.getActorName(), actor);
        return this;
    }

    public ConversationContext registerConversation(Conversation conversation) {
        this.conversationMap.put(conversation.getConversationCode(), conversation);
        return this;
    }
}
