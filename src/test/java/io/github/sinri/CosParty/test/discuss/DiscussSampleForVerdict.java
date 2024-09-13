package io.github.sinri.CosParty.test.discuss;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.CosParty.actor.Action;
import io.github.sinri.CosParty.model.discuss.Discuss;
import io.github.sinri.CosParty.model.discuss.DiscussHost;
import io.github.sinri.CosParty.model.discuss.DiscussMember;
import io.github.sinri.CosParty.test.AnySample;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class DiscussSampleForVerdict extends AnySample {
    @TestUnit
    public Future<Void> run() {
        Host host = new Host(getAnyLLMKit());
        Member memberAsProduct = new Member(
                getAnyLLMKit(),
                "产品经理代表",
                "尽可能地从产品经理是否尽责的角度找数据事故原因；产品经理的职责有与需求方沟通、与数据源侧协调、为开发人员提供正确的逻辑描述、合理配置质检规则等。",
                ""
        );
        Member memberAsETL = new Member(
                getAnyLLMKit(),
                "大数据开发人员代表",
                "尽可能地从大数据开发人员是否尽责的角度找数据事故原因；大数据开发人员的职责有准确实现产品经理给定的数据转化需求、正确配置大数据程序调度并确保集群运转等。",
                ""
        );
        Member memberAsFell = new Member(
                getAnyLLMKit(),
                "数据采集开发人员代表",
                "尽可能地从数据采集开发人员是否尽责的角度找数据事故原因；数据采集开发人员的职责有准确实现产品经理给定的数据采集需求、正确配置采集程序调度、对采集过程中的意外进行响应和尽力维修等。",
                ""
        );
        Member memberAsSource = new Member(
                getAnyLLMKit(),
                "数据采集开发人员代表",
                "尽可能地从数据源的角度，即外部平台、内部系统、数据维护人员等方面找数据事故原因；例如外部平台是否有变动、内部系统是否有实现变更、数据维护人员是否维护错数据等。",
                ""
        );

        Discuss discuss = new Discuss(
                host,
                List.of(memberAsProduct, memberAsETL, memberAsFell, memberAsSource)
        );
        discuss.setRecentContextItemCallback(action -> {
            getLogger().notice(action.actorName() + ":\n" + action.message());
            return Future.succeededFuture();
        });
        return discuss.run()
                .compose(v -> {
                    getLogger().info("FIN");
                    return Future.succeededFuture();
                });
    }

    static class Host extends DiscussHost {

        public Host(AnyLLMKit anyLLMKit) {
            super(anyLLMKit);
        }

        @Override
        public String getDiscussTopic() {
            return "讨论下面所述数据事故的原因，分析各方是否存在不足，最终给出一个主要责任方，如果存在次要责任，亦可列举。";
        }

        @Override
        public String getDiscussIntroduction() {
            return "2024年8月5日，用户投诉数据集“店铺销售观测第二版”当日炸鸡王旗舰店的数据缺失。" +
                    "经数据采集开发人员调查取证，当日数据采集程序报错，未取到数据。" +
                    "产品经理实地考察发现，炸鸡王旗舰店的买卖间谍窥伺台模块到期未续费。" +
                    "后联系运营部门，炸鸡王旗舰店的店长在2024年8月7日完成续费。" +
                    "数据采集开发人员在2024年8月8日完成补数，相应数据集之数据恢复。"; // todo
        }

        @Override
        public Future<Boolean> shouldStopDiscuss(List<Action> context) {
            return shouldStopDiscussWithIO(context);
        }

        private Future<Boolean> shouldStopDiscussWithIO(List<Action> context) {
            System.out.println(">>> host, stop discuss with [end], or give your opinion start with [~] which would be ignored \n>>>");
            this.setGuideWord(null);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String s = reader.readLine();
                System.out.println("<<<" + s + "<<<");
                if (s == null || s.equals("end")) {
                    return Future.succeededFuture(true);
                }
                if (s.startsWith("~")) {
                    String word = s.substring(1);
                    System.out.println("|||" + word + "|||");
                    this.setGuideWord(word);
                }
                return Future.succeededFuture(false);
            } catch (IOException e) {
                e.printStackTrace();
                return Future.succeededFuture(true);
            }
        }

        @Deprecated
        private Future<Boolean> shouldStopDiscussWithFile(List<Action> context) {
            var commandFile = "/Users/sinri/code/CosParty/runtime/DiscussSampleForVerdict.cmd";
            var wordFile = "/Users/sinri/code/CosParty/runtime/DiscussSampleForVerdict.txt";
            return Keel.getVertx().fileSystem().readFile(commandFile)
                    .compose(buffer -> {
                        String command = buffer.toString();
                        this.setGuideWord(null);
                        if (Objects.equals(command, "end")) {
                            return Future.succeededFuture(true);
                        } else {
                            return Keel.getVertx().fileSystem().readFile(wordFile)
                                    .compose(buffer1 -> {
                                        String word = buffer1.toString();
                                        this.setGuideWord(word);
                                        return Future.succeededFuture();
                                    })
                                    .recover(v -> {
                                        return Future.succeededFuture();
                                    })
                                    .compose(v -> {
                                        return Future.succeededFuture(false);
                                    });
                        }
                    });
        }
    }

    static class Member extends DiscussMember {
        private final String contention;
        private final String additionalRule;
        private final String actorName;

        public Member(AnyLLMKit anyLLMKit, String actorName, String contention, String additionalRule) {
            super(anyLLMKit);
            this.contention = contention;
            this.additionalRule = additionalRule;
            this.actorName = actorName;
        }

        @Override
        public String getContention() {
            return contention;
        }

        @Override
        public String getAdditionalRule() {
            return additionalRule;
        }

        @Override
        public String getActorName() {
            return actorName;
        }
    }
}
