package io.github.sinri.CosParty.test.discuss.verdict;

import io.github.sinri.CosParty.test.AnySample;
import io.github.sinri.keel.tesuto.TestUnit;
import io.vertx.core.Future;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class DiscussSampleForVerdict extends AnySample {
    @TestUnit
    public Future<Void> run() {
        // 产品经理、大数据开发、数据采集开发、数据源
        String title = "240924千牛APP竞店搜索异常和名称变更事故";
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
                                getLogger().info("Verdict " + action.actorName() + " acted");
                                return Future.succeededFuture();
                            }
                    )
                            .run()
                            .compose(result -> {
                                getLogger().info("RESULT:\n------\n" + result + "\n------\nOVER.");
                                return Future.succeededFuture();
                            });
                });
    }


}
