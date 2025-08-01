package io.github.sinri.CosParty.miku.three.action;

import io.github.sinri.CosParty.kernel.context.conversation.Actor;

public final class AffirmativeActor extends Actor {
    public static final String ACTOR_NAME = "正方";

    AffirmativeActor() {
        super();
        this.setActorName(AffirmativeActor.ACTOR_NAME);
        this.setActorInstruction("作为这场辩论的正方参加，支持辩论议题。");
    }
}
