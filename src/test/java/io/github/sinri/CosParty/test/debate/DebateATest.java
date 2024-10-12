package io.github.sinri.CosParty.test.debate;

import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.github.sinri.keel.facade.KeelInstance.Keel;

public class DebateATest {
    public void test1() throws ExecutionException, InterruptedException {
        System.out.println("io.github.sinri.CosParty.test.debate.DebateATest.test start");

        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        innerTest().compose(v -> {
            System.out.println("repeat finished");
            completableFuture.complete(null);
            return Future.succeededFuture();
        });

        completableFuture.get();
        System.out.println("io.github.sinri.CosParty.test.debate.DebateATest.test end");
    }

    private Future<Void> innerTest() {
        Keel.initializeVertxStandalone(new VertxOptions());

        return KeelAsyncKit.repeatedlyCall(routineResult -> {
            return KeelAsyncKit.sleep(1000L)
                    .compose(slept -> {
                        System.out.println("slept");
                        return Keel.getVertx().sharedData().getLocalCounter(this.getClass().getName())
                                .compose(counter -> {
                                    return counter.incrementAndGet()
                                            .compose(x -> {
                                                System.out.println("x=" + x);
                                                if (x > 5) {
                                                    routineResult.stop();
                                                }
                                                return Future.succeededFuture();
                                            });
                                });
                    });
        });
    }
}
