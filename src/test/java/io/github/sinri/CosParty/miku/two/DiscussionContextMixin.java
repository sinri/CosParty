package io.github.sinri.CosParty.miku.two;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.vertx.core.json.JsonArray;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 本类展示了一种在scene里使用context的实现方式，即将读写context的逻辑封装在mixin里，在scene类里实现mixin接口。
 * 这样scene类里可以直接调用mixin的方法，而不是直接调用context的方法。
 */
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
