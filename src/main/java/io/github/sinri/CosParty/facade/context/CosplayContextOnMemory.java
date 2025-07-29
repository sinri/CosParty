package io.github.sinri.CosParty.facade.context;

import io.github.sinri.CosParty.facade.context.conversation.ConversationContext;
import io.vertx.core.shareddata.LocalMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

class CosplayContextOnMemory implements CosplayContext {
    private final LocalMap<String, String> contextMap;
    private final List<ConversationContext> conversationContexts;

    CosplayContextOnMemory(String engineId) {
        contextMap = Keel.getVertx().sharedData().getLocalMap(engineId);
        this.conversationContexts = new ArrayList<>();
    }

    @Override
    public String readString(@Nonnull String key) {
        return contextMap.get(key);
    }

    @Override
    public CosplayContext writeString(@Nonnull String key, @Nonnull String value) {
        contextMap.put(key, value);
        return this;
    }

    @Override
    public ConversationContext getConversationContext(int conversationContextId) {
        synchronized (conversationContexts) {
            return this.conversationContexts.get(conversationContextId);
        }
    }

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
