package io.github.sinri.CosParty.miku.three.action;

import io.github.sinri.CosParty.kernel.context.conversation.Actor;

public final class NegativeActor extends Actor {
    public static final String ACTOR_NAME = "反方";

    NegativeActor() {
        super();
        this.setActorName(NegativeActor.ACTOR_NAME);
        this.setActorInstruction("作为这场辩论的反方参加，反对辩论议题。");
    }
}
