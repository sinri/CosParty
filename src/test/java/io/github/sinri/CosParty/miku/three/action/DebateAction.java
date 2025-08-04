package io.github.sinri.CosParty.miku.three.action;

import io.github.sinri.CosParty.kernel.CosplayScene;
import io.github.sinri.CosParty.kernel.context.CosplayContext;
import io.github.sinri.CosParty.kernel.context.conversation.ConversationContext;
import io.github.sinri.CosParty.miku.MikuAction;
import io.github.sinri.CosParty.miku.three.action.scenes.AfterOneRoundScene;
import io.github.sinri.CosParty.miku.three.action.scenes.DebateEndScene;
import io.github.sinri.CosParty.miku.three.action.scenes.DebateStartScene;
import io.github.sinri.CosParty.miku.three.action.scenes.OneRoundScene;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DebateAction extends MikuAction {

    public static final String FIELD_DEBATE_MOTION = "debate_motion";
    public static final String FIELD_DEBATE_CONCLUSION = "debate_conclusion";
    public static final String FIELD_CONVERSATION_CODE = "conversation_code";
    public static final String FIELD_ROUND_COUNT = "round_count";
    public static final String FIELD_END_FLAG = "end_flag";

    public final ModeratorActor moderatorActor = new ModeratorActor();
    public final AffirmativeActor affirmativeActor = new AffirmativeActor();
    public final NegativeActor negativeActor = new NegativeActor();

    public DebateAction() {
        super();
    }


    @Override
    protected List<Class<? extends CosplayScene>> initializeRelatedScenes() {
        List<Class<? extends CosplayScene>> list = new ArrayList<>();
        list.add(DebateStartScene.class);
        list.add(OneRoundScene.class);
        list.add(AfterOneRoundScene.class);
        list.add(DebateEndScene.class);
        return list;
    }

    @Nonnull
    @Override
    public String getMappedFieldNameInOuterScope(@Nonnull String actionContextFieldName) {
        return "ACTION@" + actionContextFieldName;
    }

    @Nullable
    @Override
    public String seekNextSceneInAction(@Nonnull String currentSceneCode) {
        if (Objects.equals(currentSceneCode, this.getSceneCode())) {
            return DebateStartScene.class.getName();
        } else if (Objects.equals(currentSceneCode, DebateStartScene.class.getName())) {
            return OneRoundScene.class.getName();
        } else if (Objects.equals(currentSceneCode, OneRoundScene.class.getName())) {
            return AfterOneRoundScene.class.getName();
        } else if (Objects.equals(currentSceneCode, AfterOneRoundScene.class.getName())) {
            Integer endFlag = getContextOnThisActionScope().readInteger(DebateAction.FIELD_END_FLAG);
            if (endFlag != null && endFlag == 1) {
                return DebateEndScene.class.getName();
            } else {
                return OneRoundScene.class.getName();
            }
        } else if (Objects.equals(currentSceneCode, DebateEndScene.class.getName())) {
            return null;
        }
        throw new IllegalStateException();
    }

    @Override
    public void input(@Nonnull CosplayContext outerScopeContext) {
        String outerFieldForDebateMotion = getMappedFieldNameInOuterScope(DebateAction.FIELD_DEBATE_MOTION);
        System.out.println("outerScopeContext: " + outerScopeContext.getContextId());
        System.out.println("key: " + outerFieldForDebateMotion);
        getContextOnThisActionScope().pullFromAnotherContext(
                outerScopeContext,
                outerFieldForDebateMotion,
                DebateAction.FIELD_DEBATE_MOTION
        );
        ConversationContext conversationContext = getContextOnThisActionScope().createConversationContext();
        conversationContext.registerActor(moderatorActor)
                           .registerActor(affirmativeActor)
                           .registerActor(negativeActor);
    }

    @Override
    public void output(@Nonnull CosplayContext outerScopeContext) {
        outerScopeContext.pullFromAnotherContext(
                this.getContextOnThisActionScope(),
                DebateAction.FIELD_DEBATE_CONCLUSION,
                getMappedFieldNameInOuterScope(DebateAction.FIELD_DEBATE_CONCLUSION)
        );
    }

    @Nonnull
    @Override
    protected CosplayContext buildContextForThisActionScope() {
        return CosplayContext.withMemory();
    }
}
