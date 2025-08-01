package io.github.sinri.CosParty.miku.two;

import io.github.sinri.CosParty.kernel.context.conversation.DynamicActor;
import io.github.sinri.CosParty.miku.MikuEngine;
import io.github.sinri.drydock.naval.raider.Privateer;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.Map;

public class Discussion extends Privateer {

    @Override
    protected Future<Void> launchAsPrivateer() {
        DiscussionScript script = new DiscussionScript();
        script.addScene(StartScene.class)
              .addScene(OneRoundScene.class)
              .addScene(AfterOneRoundScene.class)
              .addScene(EndScene.class);
        script.confirmStartScene(StartScene.class);

        MikuEngine engine = new MikuEngine(script);
        return engine.startup(Map.of(
                DiscussionScript.FIELD_TOPIC, "在常规体量的Java Web项目开发中，Quarkus、Javalin、Vert.x、Spring 应如何选型？",
                DiscussionScript.FIELD_MEMBERS, new JsonArray()
                        .add(new DynamicActor()
                                .setActorName("老黑")
                                .setActorInstruction("拥有很长的企业项目开发经验，特别关注系统稳定性。")
                        )
                        .add(new DynamicActor()
                                .setActorName("小白")
                                .setActorInstruction("新近上手开发工作，注重跟进最新技术，考虑发展前途。")
                        )
                        .add(new DynamicActor()
                                .setActorName("大华")
                                .setActorInstruction("从事技术管理工作，关心代码维护和人员储备方案。")
                        )
                        .toString()
        ));
    }
}
