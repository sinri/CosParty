package io.github.sinri.CosParty.miku.three.action;

import io.github.sinri.CosParty.kernel.context.conversation.Actor;

public final class ModeratorActor extends Actor {
    public static final String ACTOR_NAME = "主持人";

    ModeratorActor() {
        super();
        this.setActorName(ModeratorActor.ACTOR_NAME);
        this.setActorInstruction("主持本次辩论，把握节奏，并在最后进行总结。");
    }
}
