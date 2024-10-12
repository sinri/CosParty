package io.github.sinri.CosParty.test.discuss.verdict;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseChoice;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseToolFunctionCall;
import io.github.sinri.CosParty.actor.Action;
import io.github.sinri.CosParty.model.discuss.Discuss;
import io.github.sinri.CosParty.model.discuss.DiscussHost;
import io.github.sinri.CosParty.model.discuss.DiscussMember;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Verdict {
    private final String accidentDetail;
    private final Discuss discuss;

    public Verdict(AnyLLMKit anyLLMKit,
                   String accidentDetail,
                   @Nullable Function<Action, Future<Void>> recentContextItemCallback
    ) {
        this.accidentDetail = accidentDetail;
        var host = new Host(anyLLMKit,
                """
                        基于下面所述数据事故，\
                        从产品经理、大数据开发、数据采集开发、数据源四个不同岗位职责出发分析原因，\
                        讨论分析是哪一方因为没有完成职责导致数据事故发生，以此为主要责任方。
                        此外，如果有需要负次要责任的也可列出。
                        一般地，涉及数据源异常的事故，在没有明确的针对性保障机制的情况下，优先考虑数据源的责任。""",
                accidentDetail
        );

        Map<String, String> memberMetaMap = new TreeMap<>();
        memberMetaMap.put(
                "数据源维护人员代表",
                "尽可能地从数据源的角度，即外部平台、内部系统、数据维护人员等方面找数据事故原因；" +
                        "例如外部平台页面和结果是否有变动、外部平台有没有阻碍登录或废弃会话的动作、内部系统是否有实现变更、数据维护人员是否维护错数据等。"
        );
        memberMetaMap.put(
                "产品经理代表",
                "尽可能地从产品经理是否尽责的角度找数据事故原因，优先审视有无需求描述和理解方面的问题；" +
                        "产品经理的职责有与需求方沟通、与数据源侧协调、为开发人员提供正确的逻辑描述、合理配置质检规则等。"
        );
        memberMetaMap.put(
                "大数据开发人员代表",
                "尽可能地从大数据开发人员是否尽责的角度找数据事故原因；" +
                        "大数据开发人员的职责有准确实现产品经理给定的数据转化需求、正确配置大数据程序调度并确保集群运转等。" +
                        "一般来说，作为开发人员，仅为基于给定需求的代码实现和配置设定负责；" +
                        "凡需求有问题，上游数据源发生变动或缺失，如数据采集失败、上游系统变更数据格式等，属于大数据开发的能力范围外，不承担责任。"
        );
        memberMetaMap.put(
                "数据采集开发人员代表",
                "尽可能地从数据采集开发人员是否尽责的角度找数据事故原因；" +
                        "数据采集开发人员的职责有准确实现产品经理给定的数据采集需求、正确配置采集程序调度、对采集过程中的意外进行响应和尽力维修等。" +
                        "一般来说，作为开发人员，仅为基于给定需求的代码实现和配置设定负责；" +
                        "凡需求有问题，上游数据源发生变动或缺失，如外部平台变更、平台风控导致登录不上会话失效导致数据采集失败等，属于数据采集开发的能力范围外，不承担责任。"
        );


        List<Member> members = new ArrayList<>();
        memberMetaMap.forEach((memberName, memberContention) -> {
            members.add(new Member(
                    anyLLMKit,
                    memberName,
                    memberContention,
                    "发言要简明扼要，无需客套。"
            ));
        });

        this.discuss = new Discuss(host, members);
        discuss.setRecentContextItemCallback(recentContextItemCallback);
    }

    public Future<String> run() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("# ACCIDENT VERDICT\n\n");
        return this.discuss.run()
                .compose(actions -> {
//                    actions.forEach(action -> {
//                        sb.append("## Saith ").append(action.actorName()).append("\n\n")
//                                .append(action.message()).append("\n\n");
//                    });
                    Action lastAction = actions.get(actions.size() - 1);
                    return Future.succeededFuture(lastAction.message());
                });
    }

    private static class Host extends DiscussHost {
        private final String topic;
        private final String introduction;
        private final AtomicInteger roundCounter = new AtomicInteger(0);

        public Host(AnyLLMKit anyLLMKit, String topic, String introduction) {
            super(anyLLMKit);
            this.topic = topic;
            this.introduction = introduction;
        }

        @Override
        public String getDiscussTopic() {
            return topic;
        }

        @Override
        public String getDiscussIntroduction() {
            return introduction;
        }

        @Override
        public Future<Boolean> shouldStopDiscuss(List<Action> context) {
            return Future.succeededFuture(true);
//            if (roundCounter.incrementAndGet() > 2) {
//                return Future.succeededFuture(true);
//            } else {
//                return Future.succeededFuture(false);
//            }
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

        @Override
        protected Future<String> actToStop(List<Action> context) {
            var s = "本场讨论已经结束，请综合上面的讨论给出总结和最终的结论；输出一个JSON对象文本，不要有多余内容。\n输出样例如下：\n" +
                    (new JsonObject()
                            .put("conclusion", "讨论过程的总结")
                            .put("fc", new JsonObject()
                                    .put("major_side", "主要责任方")
                                    .put("major_side_reason", "主要责任方的定责原因")
                                    .put("minor_side", "次要责任方")
                                    .put("minor_side_reason", "次要责任方的定责原因")
                            )
                            .toString()
                    );
            return this.applyToLLM(context, s, req -> {
                    })
                    .compose(response -> {
//                        JsonObject result = new JsonObject();
//
//                        List<AnyLLMResponseChoice> choices = response.getChoices();
//                        AnyLLMResponseChoice anyLLMResponseChoice = choices.get(0);
//                        var fc_call_list = anyLLMResponseChoice.getFunctionCalls();
//                        JsonObject fc = new JsonObject();
//                        if (fc_call_list != null && !fc_call_list.isEmpty()) {
//                            try {
//                                AnyLLMResponseToolFunctionCall anyLLMResponseToolFunctionCall = fc_call_list.get(0);
//                                String functionName = anyLLMResponseToolFunctionCall.getFunctionName();
//                                String functionArguments = anyLLMResponseToolFunctionCall.getFunctionArguments();
//
//                                JsonObject parsed = new JsonObject(functionArguments);
//                                String major_side = parsed.getString("major_side");
//                                String major_side_reason = parsed.getString("major_side_reason");
//                                String minor_side = parsed.getString("minor_side");
//                                String minor_side_reason = parsed.getString("minor_side_reason");
//
//                                fc.put("major_side", major_side)
//                                        .put("major_side_reason", major_side_reason)
//                                        .put("minor_side", minor_side)
//                                        .put("minor_side_reason", minor_side_reason);
//                                ;
//                            } catch (Exception e) {
//                                fc.put("exception", e.getMessage());
//                            }
//                        } else {
//                            fc = null;
//                        }
//
//                        result.put("content", anyLLMResponseChoice.getContent())
//                                .put("fc", fc);
//
//                        return Future.succeededFuture(result.toString());

                        return Future.succeededFuture(response.getChoices().get(0).getContent());
                    });
        }

        protected Future<String> actToStop_v2(List<Action> context) {
            var s = "本场讨论已经结束，请综合上面的讨论给出总结和最终的结论，并给出该结论是否符合规范。";
            return this.applyToLLM(context, s, req -> {
                        req.addFunctionToolDefinition(builder -> builder
                                .functionName("record_accident_result")
                                .functionDescription("根据给定的参数中的数据事故的职责判定结果，返回这个结果是否符合规范。")
                                .propertyAsString("major_side", "一个字符串，即唯一的一个主要责任方")
                                .propertyAsString("major_side_reason", "一个字符串，主要责任方定责的解释说明")
                                .propertyAsString("minor_side", "一个字符串，即次要责任方，可能有零个或多个，可用英文逗号隔开")
                                .propertyAsString("minor_side_reason", "一个字符串，次要责任方定责的解释说明")
                        );
                    })
                    .compose(response -> {
                        JsonObject result = new JsonObject();

                        List<AnyLLMResponseChoice> choices = response.getChoices();
                        AnyLLMResponseChoice anyLLMResponseChoice = choices.get(0);
                        var fc_call_list = anyLLMResponseChoice.getFunctionCalls();
                        JsonObject fc = new JsonObject();
                        if (fc_call_list != null && !fc_call_list.isEmpty()) {
                            try {
                                AnyLLMResponseToolFunctionCall anyLLMResponseToolFunctionCall = fc_call_list.get(0);
                                String functionName = anyLLMResponseToolFunctionCall.getFunctionName();
                                String functionArguments = anyLLMResponseToolFunctionCall.getFunctionArguments();

                                JsonObject parsed = new JsonObject(functionArguments);
                                String major_side = parsed.getString("major_side");
                                String major_side_reason = parsed.getString("major_side_reason");
                                String minor_side = parsed.getString("minor_side");
                                String minor_side_reason = parsed.getString("minor_side_reason");

                                fc.put("major_side", major_side)
                                        .put("major_side_reason", major_side_reason)
                                        .put("minor_side", minor_side)
                                        .put("minor_side_reason", minor_side_reason);
                            } catch (Exception e) {
                                fc.put("exception", e.getMessage());
                            }
                        } else {
                            fc = null;
                        }

                        result.put("content", anyLLMResponseChoice.getContent())
                                .put("fc", fc);

                        return Future.succeededFuture(result.toString());
                    });
        }
    }

    private static class Member extends DiscussMember {
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
