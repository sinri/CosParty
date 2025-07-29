package io.github.sinri.CosParty.facade.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableEntity;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 在一个{@link ConversationContext}下进行的一场连续的对话。
 */
public class Conversation implements JsonifiableEntity<Conversation> {
    @Nonnull
    private final List<Speech> speechList;
    private String conversationCode;

    public Conversation() {
        this.speechList = new ArrayList<>();
        this.conversationCode = UUID.randomUUID().toString();
    }

    public String getConversationCode() {
        return conversationCode;
    }

    public Conversation addSpeech(@Nonnull Speech speech) {
        this.speechList.add(speech);
        return this;
    }

    public Iterable<Speech> getIterableOfSpeechList() {
        return this.speechList;
    }

    @Nonnull
    @Override
    public JsonObject toJsonObject() {
        return new JsonObject()
                .put("conversation_code", conversationCode)
                .put("speech_list", new JsonArray(speechList))
                ;
    }

    @Nonnull
    @Override
    public Conversation reloadDataFromJsonObject(@Nonnull JsonObject jsonObject) {
        var c = new Conversation();
        c.conversationCode = (jsonObject.getString("conversation_code"));
        JsonArray array = jsonObject.getJsonArray("speech_list");
        array.forEach(item -> {
            var o = ((JsonObject) item);
            Speech speech = new Speech().reloadDataFromJsonObject(o);
            c.addSpeech(speech);
        });
        return c;
    }

    @Nonnull
    @Override
    public Conversation getImplementation() {
        return this;
    }

    @Override
    public String toString() {
        return toJsonExpression();
    }
}
