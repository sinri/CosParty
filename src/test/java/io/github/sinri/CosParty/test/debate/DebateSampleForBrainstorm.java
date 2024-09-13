package io.github.sinri.CosParty.test.debate;

import io.github.sinri.AiOnHttpMix.mix.AnyLLMKit;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseChoice;
import io.github.sinri.AiOnHttpMix.mix.AnyLLMResponseToolFunctionCall;
import io.github.sinri.CosParty.actor.Action;
import io.github.sinri.CosParty.model.debate.Debate;
import io.github.sinri.CosParty.model.debate.DebateHost;
import io.github.sinri.CosParty.model.debate.Debater;
import io.github.sinri.CosParty.test.AnySample;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

/**
 * 本样例使用纯AI进行了辩论型脑暴，并能给出总结。
 */
public class DebateSampleForBrainstorm extends AnySample {


    @TestUnit
    public Future<Void> debate() {
        TheDebateHost theDebateHost = new TheDebateHost(getAnyLLMKit());
        TheDebaterAsDev theDebaterAsDev = new TheDebaterAsDev(getAnyLLMKit());
        TheDebaterAsPM theDebaterAsPM = new TheDebaterAsPM(getAnyLLMKit());
        TheDebaterAsUser theDebaterAsUser = new TheDebaterAsUser(getAnyLLMKit());
        return new Debate(
                theDebateHost,
                List.of(theDebaterAsDev, theDebaterAsPM, theDebaterAsUser)
        )
                .setRecentContextItemCallback(contextItem -> {
                    getLogger().notice(contextItem.actorName() + ":\n" + contextItem.message());
                    return Future.succeededFuture();
                })
                .run()
                .compose(context -> {
                    getLogger().info("FIN");
                    return Future.succeededFuture();
                });
    }

    private static class TheDebateHost extends DebateHost {
        public TheDebateHost(AnyLLMKit anyLLMKit) {
            super(anyLLMKit);
        }

        @Override
        public String getDebateTopic() {
            return "电商企业大数据建设过程中数据治理应该由开发人员主导还是由产品经理主导？";
        }

        @Override
        public Future<Boolean> shouldStopDebate(List<Action> context) {
            if (context.size() > 6 && context.size() < 18) {
                var fn = "setShouldDebateTerminate";
                return this.applyToLLM(
                                context,
                                req -> req
                                        .addFunctionToolDefinition(b -> b
                                                .functionName(fn)
                                                .functionDescription("每轮辩论完毕后调用，设置是否结束辩论并给出主持人发言。每场辩论应至少进行三轮，在相对完整地讨论各种情况后才能结束。")
                                                .propertyAsBoolean("should_terminate", "Boolean。必填。不可为空。如果为true则应结束辩论，为false则辩论应继续。")
                                                .propertyAsString("round_conclusion", "String。必填。不可为空。每回合辩论结束后主持人发表的总结。")
                                        )
                                        .addUserMessage("作为主持人，判断一下当前的辩论内容是否已经足够丰富，确定是否结束辩论。请以此调用指定函数。")
                        )
                        .compose(anyLLMResponse -> {
                            Keel.getLogger().info("shouldStopDebate by LLM: " + anyLLMResponse.toString());
                            List<AnyLLMResponseChoice> choices = anyLLMResponse.getChoices();
                            AnyLLMResponseChoice anyLLMResponseChoice = choices.get(0);

                            List<AnyLLMResponseToolFunctionCall> toolCalls = anyLLMResponseChoice.getFunctionCalls();
                            if (toolCalls == null || toolCalls.isEmpty()) {
                                Keel.getLogger().fatal("shouldStopDebate, LLM did not call tool function");
                                return Future.failedFuture("shouldStopDebate, LLM did not call tool function");
                            }
                            AnyLLMResponseToolFunctionCall functionCall = toolCalls.get(0);
                            String argument = functionCall.getFunctionArguments();
                            Keel.getLogger().info("shouldStopDebate: " + functionCall.getFunctionName() + " with " + argument);
                            if (argument == null) {
                                Keel.getLogger().error("shouldStopDebate, LLM did not get arguments");
                                return Future.succeededFuture(false);
                            } else {
                                JsonObject j = new JsonObject(argument);
                                boolean shouldTerminate = j.getBoolean("should_terminate");
                                Keel.getLogger().info("shouldStopDebate: shouldTerminate=" + shouldTerminate);

                                String round_conclusion = j.getString("round_conclusion");
                                if (round_conclusion != null) {
                                    Keel.getLogger().notice("round_conclusion: " + round_conclusion);
                                }

                                return Future.succeededFuture(shouldTerminate);
                            }
                        });
            } else {
                return Future.succeededFuture(false);
            }
        }
    }

    private abstract static class TheDebater extends Debater {
        public TheDebater(AnyLLMKit anyLLMKit) {
            super(anyLLMKit);
        }

        @Override
        public String getAdditionalRule() {
            return "应真实客观提出论点，简明扼要，并提供必要的论据。每次发言不超过150字。";
        }
    }

    private static class TheDebaterAsDev extends TheDebater {
        public TheDebaterAsDev(AnyLLMKit anyLLMKit) {
            super(anyLLMKit);
        }

        @Override
        public String getActorName() {
            return "技术驱动派";
        }

        @Override
        public String getContention() {
            return "电商企业大数据建设过程中数据治理应该由技术驱动，开发人员自行执行";
        }
    }

    private static class TheDebaterAsPM extends TheDebater {
        public TheDebaterAsPM(AnyLLMKit anyLLMKit) {
            super(anyLLMKit);
        }

        @Override
        public String getActorName() {
            return "产品规划驱动派";
        }

        @Override
        public String getContention() {
            return "电商企业大数据建设过程中数据治理应该由产品规划驱动，由产品经理进行规划";
        }

    }

    private static class TheDebaterAsUser extends TheDebater {
        public TheDebaterAsUser(AnyLLMKit anyLLMKit) {
            super(anyLLMKit);
        }

        @Override
        public String getActorName() {
            return "业务驱动派";
        }

        @Override
        public String getContention() {
            return "电商企业大数据建设过程中数据治理应该由业务驱动，由产品经理按照业务的要求实施";
        }

    }
}
