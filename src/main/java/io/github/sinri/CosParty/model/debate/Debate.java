package io.github.sinri.CosParty.model.debate;

import io.github.sinri.CosParty.actor.Actor;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Debate {
    private final DebateHost host;
    private final List<Debater> debaters;
    private final List<Actor.ContextItem> context;

    @Nullable
    private Function<Actor.ContextItem, Future<Void>> recentContextItemCallback;

    public Debate(DebateHost host, List<Debater> debaters) {
        this.host = host;
        this.debaters = debaters;
        context = new ArrayList<>();
        recentContextItemCallback = null;
    }

    public Debate setRecentContextItemCallback(Function<Actor.ContextItem, Future<Void>> recentContextItemCallback) {
        this.recentContextItemCallback = recentContextItemCallback;
        return this;
    }

    private Future<Void> executeCallbackForRecentWord(Actor.ContextItem contextItem) {
        if (this.recentContextItemCallback != null) {
            return this.recentContextItemCallback.apply(contextItem);
        }
        return Future.succeededFuture();
    }

    public Future<List<Actor.ContextItem>> run() {
        var start = new Actor.ContextItem(
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
                    return host.thinkAndSpeak(context);
                })
                .compose(conclusion -> {
                    Actor.ContextItem contextItem = new Actor.ContextItem(host.getActorName(), conclusion);
                    context.add(contextItem);
                    executeCallbackForRecentWord(contextItem);
                    return Future.succeededFuture(context);
                });
    }

    private Future<Boolean> round() {
        return KeelAsyncKit.iterativelyCall(debaters, debater -> {
                    return debater.thinkAndSpeak(context)
                            .compose(resp -> {
                                Actor.ContextItem x = new Actor.ContextItem(debater.getActorName(), resp);
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
