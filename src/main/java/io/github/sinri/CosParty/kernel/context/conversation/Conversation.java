package io.github.sinri.CosParty.kernel.context.conversation;

import io.github.sinri.keel.core.json.JsonifiableDataUnit;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 对话类。
 * <p>
 * 包含多个角色按时间顺序发表的言论。
 */
public class Conversation implements JsonifiableDataUnit {
    @Nonnull
    private final List<Speech> speechList;

    private String conversationCode;

    public Conversation() {
        this.speechList = new ArrayList<>();
        this.conversationCode = UUID.randomUUID().toString();
    }

    /**
     * 获取对话的唯一标识符。
     *
     * @return 对话代码
     */
    public String getConversationCode() {
        return conversationCode;
    }

    /**
     * 添加发言到对话中。
     *
     * @param speech 要添加的发言
     * @return 当前对话实例
     */
    public Conversation addSpeech(@Nonnull Speech speech) {
        this.speechList.add(speech);
        return this;
    }

    /**
     * 获取对话中的所有发言。
     *
     * @return 按时间顺序的发言列表
     */
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

    @Override
    public void reloadData(@Nonnull JsonObject jsonObject) {
        this.conversationCode = (jsonObject.getString("conversation_code"));
        this.speechList.clear();
        JsonArray array = jsonObject.getJsonArray("speech_list");
        array.forEach(item -> {
            var o = ((JsonObject) item);
            Speech speech = new Speech();
            speech.reloadData(o);
            this.addSpeech(speech);
        });
    }

    @Override
    public String toString() {
        return toJsonExpression();
    }
}
