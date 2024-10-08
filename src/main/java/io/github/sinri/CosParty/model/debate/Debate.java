package io.github.sinri.CosParty.model.debate;

import io.github.sinri.CosParty.actor.Action;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 让一个AI主持，若干AI互相辩论的模型。
 */
public class Debate {
    private final DebateHost host;
    private final List<Debater> debaters;
    private final List<Action> context;

    @Nullable
    private Function<Action, Future<Void>> recentContextItemCallback;

    public Debate(DebateHost host, List<Debater> debaters) {
        this.host = host;
        this.debaters = debaters;
        context = new ArrayList<>();
        recentContextItemCallback = null;
    }

    public Debate setRecentContextItemCallback(Function<Action, Future<Void>> recentContextItemCallback) {
        this.recentContextItemCallback = recentContextItemCallback;
        return this;
    }

    private Future<Void> executeCallbackForRecentWord(Action action) {
        if (this.recentContextItemCallback != null) {
            return this.recentContextItemCallback.apply(action);
        }
        return Future.succeededFuture();
    }

    public Future<List<Action>> run() {
        var start = new Action(
                host.getActorName(),
                "本次辩论的主题为：" + host.getDebateTopic() + "\n" +
                        "请大家依次发言。"
        );
        context.add(start);
        executeCallbackForRecentWord(start);

        return KeelAsyncKit.repeatedlyCall(routineResult -> {
                    return round()
                            .compose(shouldStop -> {
                                if (shouldStop) {
                                    routineResult.stop();
                                }
                                return Future.succeededFuture();
                            });
                })
                .compose(v -> {
                    return host.act(context);
                })
                .compose(conclusion -> {
                    if (conclusion != null) {
                        Action action = new Action(host.getActorName(), conclusion);
                        context.add(action);
                        executeCallbackForRecentWord(action);
                    }
                    return Future.succeededFuture(context);
                });
    }

    private Future<Boolean> round() {
        return KeelAsyncKit.iterativelyCall(debaters, debater -> {
                    return debater.act(context)
                            .compose(resp -> {
                                Action x = new Action(debater.getActorName(), resp);
                                context.add(x);
                                executeCallbackForRecentWord(x);
                                return Future.succeededFuture();
                            });
                })
                .compose(v -> {
                    return host.shouldStopDebate(context);
                });
    }
}
