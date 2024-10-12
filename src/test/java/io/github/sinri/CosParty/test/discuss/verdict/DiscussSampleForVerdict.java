package io.github.sinri.CosParty.test.discuss.verdict;

import io.github.sinri.CosParty.test.AnySample;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class DiscussSampleForVerdict extends AnySample {
    @TestUnit
    public Future<Void> run() {
        // 产品经理、大数据开发、数据采集开发、数据源
        String title = "241009未维护维表导致情感标签打标有误事故";
        //"20241008飞瓜平台取数异常事故";
        //"240929";
        //"240924千牛APP竞店搜索异常和名称变更事故";
        //"240918ETL任务跑数失败事故";
//        String p = "/Users/sinri/code/CosParty/runtime/verdict/240722.md"; // 数据源
        String p = "/Users/sinri/code/CosParty/runtime/verdict/" + title + ".md"; // 产品经理、大数据开发
        return Keel.getVertx().fileSystem().readFile(p)
                .compose(buffer -> {
                    String accidentDetail = buffer.toString();

                    return new Verdict(
                            getAnyLLMKit(),
                            accidentDetail,
                            action -> {
                                getLogger().info("As " + action.actorName() + " saith:\n" + action.message());
                                return Future.succeededFuture();
                            }
                    )
                            .run()
                            .compose(result -> {
                                try {
                                    JsonObject parsed = new JsonObject(result);
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("总结：").append(parsed.getString("conclusion")).append("\n");
                                    var fc = parsed.getJsonObject("fc");
                                    if (fc != null) {
                                        sb.append("主要责任方：").append(fc.getString("major_side")).append("\n")
                                                .append("原因：").append(fc.getString("major_side_reason")).append("\n")
                                                .append("次要责任方：").append(fc.getString("minor_side")).append("\n")
                                                .append("原因：").append(fc.getString("minor_side_reason")).append("\n");
                                    }
                                    getLogger().info("RESULT:\n------\n" + sb + "\n------\nOVER.");
                                    return Future.succeededFuture();
                                } catch (Exception e) {
                                    getLogger().info("UNPARSED: \n" + result);
                                    getLogger().exception(e);
                                    return Future.failedFuture(e);
                                }
                            });
                });
    }


}
