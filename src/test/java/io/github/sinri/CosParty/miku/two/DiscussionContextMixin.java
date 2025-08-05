package io.github.sinri.CosParty.miku.two;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.vertx.core.json.JsonArray;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface DiscussionContextMixin {
    public static final String FIELD_TOPIC = "topic";
    public static final String FIELD_MEMBERS = "members";
    public static final String FIELD_CONVERSATION_CONTEXT_ID = "conversation_context_id";
    public static final String FIELD_CONVERSATION_CODE = "conversation_code";
    public static final String FIELD_ROUND_COUNT = "round_count";
    public static final String FIELD_END_FLAG = "end_flag";

    default void topic(@Nonnull String topic) {
        this.context().writeString(FIELD_TOPIC, topic);
    }

    @Nullable
    default String topic() {
        return this.context().readString(FIELD_TOPIC);
    }

    default void members(@Nonnull JsonArray members) {
        this.context().writeJsonArray(FIELD_MEMBERS, members);
    }

    @Nullable
    default JsonArray members() {
        return this.context().readJsonArray(FIELD_MEMBERS);
    }

    default void conversationContextId(int conversation_context_id) {
        this.context().writeNumber(FIELD_CONVERSATION_CONTEXT_ID, conversation_context_id);
    }

    @Nullable
    default Integer conversationContextId() {
        return this.context().readInteger(FIELD_CONVERSATION_CONTEXT_ID);
    }

    default void conversationCode(String conversation_code) {
        this.context().writeString(FIELD_CONVERSATION_CODE, conversation_code);
    }

    @Nullable
    default String conversationCode() {
        return this.context().readString(FIELD_CONVERSATION_CODE);
    }

    @Deprecated
    private void roundCount(int round_count) {
        this.context().writeNumber(FIELD_ROUND_COUNT, round_count);
    }

    default void increaseRoundCount() {
        this.context().computedInteger(FIELD_ROUND_COUNT, x -> Objects.requireNonNullElse(x, 0) + 1);
    }

    @Nullable
    default Integer roundCount() {
        return this.context().readInteger(FIELD_ROUND_COUNT);
    }

    default void endFlag(int endFlag) {
        this.context().writeNumber(FIELD_END_FLAG, endFlag);
    }

    @Nullable
    default Integer endFlag() {
        return this.context().readInteger(FIELD_END_FLAG);
    }

    CosplayContext context() throws IllegalStateException;
}
