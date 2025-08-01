package io.github.sinri.CosParty.miku.two;

import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.CosParty.miku.MikuScript;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DiscussionScript extends MikuScript {
    public static final String FIELD_TOPIC = "topic";
    public static final String FIELD_MEMBERS = "members";
    public static final String FIELD_CONVERSATION_CONTEXT_ID = "conversation_context_id";
    public static final String FIELD_CONVERSATION_CODE = "conversation_code";
    public static final String FIELD_ROUND_COUNT = "round_count";
    public static final String FIELD_END_FLAG = "end_flag";

    @Nullable
    @Override
    public String seekNextSceneInScript(@Nonnull CosplayContext contextOnScriptScope, @Nonnull String currentSceneCode) {
        if (currentSceneCode.equals(StartScene.class.getName())) {
            return OneRoundScene.class.getName();
        } else if (currentSceneCode.equals(OneRoundScene.class.getName())) {
            return AfterOneRoundScene.class.getName();
        } else if (currentSceneCode.equals(AfterOneRoundScene.class.getName())) {
            Integer endFlag = contextOnScriptScope.readInteger(DiscussionScript.FIELD_END_FLAG);
            if (endFlag != null && endFlag == 1) {
                return EndScene.class.getName();
            } else {
                return OneRoundScene.class.getName();
            }
        } else if (currentSceneCode.equals(EndScene.class.getName())) {
            return null;
        }
        throw new IllegalStateException();
    }
}
