package io.github.sinri.CosParty.test;

import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.request.OpenAIChatGptRequestToolChoiceOption;
import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.response.OpenAIChatGptResponseFunctionCall;
import io.github.sinri.AiOnHttpMix.azure.openai.chatgpt.response.OpenAIChatGptResponseToolCall;
import io.github.sinri.CosParty.model.debate.Debate;
import io.github.sinri.CosParty.model.debate.DebateHost;
import io.github.sinri.CosParty.model.debate.Debater;
import io.github.sinri.keel.logger.KeelLogLevel;
import io.github.sinri.keel.tesuto.KeelTest;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class DebateSample extends KeelTest {
    @Override
    protected @NotNull Future<Void> starting() {
        return super.starting()
                .compose(v -> {
                    Keel.getConfiguration().loadPropertiesFile("config.properties");
                    Keel.getLogger().setVisibleLevel(KeelLogLevel.NOTICE);

//                    AigcMix.enableVerboseLogger(KeelLogLevel.DEBUG);

                    return Future.succeededFuture();
                });
    }

    @TestUnit
    public Future<Void> debate() {
        TheDebateHost theDebateHost = new TheDebateHost();
        TheDebaterAsDev theDebaterAsDev = new TheDebaterAsDev();
        TheDebaterAsPM theDebaterAsPM = new TheDebaterAsPM();
        TheDebaterAsUser theDebaterAsUser = new TheDebaterAsUser();
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


        @Override
        public String getDebateTopic() {
            return "电商企业大数据建设过程中数据治理应该由开发人员主导还是由产品经理主导？";
        }

        @Override
        public Future<Boolean> shouldStopDebate(List<ContextItem> context) {
            if (context.size() > 6 && context.size() < 18) {
                var fn = "setShouldDebateTerminate";
                return this.applyToLLM(
                                context,
                                req -> req
                                        .addTool(b -> b
                                                .functionName(fn)
                                                .functionDescription("每轮辩论完毕后调用，设置是否结束辩论并给出主持人发言。每场辩论应至少进行三轮，在相对完整地讨论各种情况后才能结束。")
                                                .propertyAsBoolean("should_terminate", "Boolean。必填。不可为空。如果为true则应结束辩论，为false则辩论应继续。")
                                                .propertyAsString("round_conclusion", "String。必填。不可为空。每回合辩论结束后主持人发表的总结。")
                                        )
                                        .setToolChoice(OpenAIChatGptRequestToolChoiceOption.asFunction(fn))
                                        .addMessage(b -> b.user("作为主持人，判断一下当前的辩论内容是否已经足够丰富，确定是否结束辩论。请以此调用指定函数。"))
                        )
                        .compose(am -> {
                            Keel.getLogger().info("shouldStopDebate by LLM", am.toJsonObject());
                            List<OpenAIChatGptResponseToolCall> toolCalls = am.getToolCalls();
                            if (toolCalls == null || toolCalls.isEmpty()) {
                                Keel.getLogger().fatal("shouldStopDebate, LLM did not call tool function", am.toJsonObject());
                                return Future.failedFuture("shouldStopDebate, LLM did not call tool function");
                            }
                            OpenAIChatGptResponseToolCall toolCall = toolCalls.get(0);
                            OpenAIChatGptResponseFunctionCall functionCall = toolCall.getFunction();
                            String argument = functionCall.getArguments();
                            Keel.getLogger().info("shouldStopDebate: " + functionCall.getName() + " with " + argument);
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

//                return this.applyToLLM(context, "作为主持人，判断一下当前的辩论内容是否已经足够丰富。如果辩论内容已经足够详实，回复“结束”，否则回复“继续”。")
//                        .compose(s -> {
//                            Keel.getLogger().fatal("shouldStopDebate by LLM: " + s);
//                            if (s.contains("结束")) {
//                                return Future.succeededFuture(true);
//                            }
//                            return Future.succeededFuture(false);
//                        });
            } else {
                return Future.succeededFuture(false);
            }
            //return Future.succeededFuture(context.size() > 10);
        }
    }

    private abstract static class TheDebater extends Debater {
        @Override
        public String getAdditionalRule() {
            return "应真实客观提出论点，简明扼要，并提供必要的论据。每次发言不超过150字。";
        }
    }

    private static class TheDebaterAsDev extends TheDebater {
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
