package io.github.sinri.CosParty.model.discuss;

import io.github.sinri.CosParty.actor.Action;
import io.github.sinri.keel.facade.async.KeelAsyncKit;
import io.vertx.core.Future;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Discuss {
    private final DiscussHost host;
    private final List<DiscussMember> members;
    private final List<Action> context;

    @Nullable
    private Function<Action, Future<Void>> recentContextItemCallback;

    public Discuss(DiscussHost host, List<DiscussMember> members) {
        this.host = host;
        this.members = members;
        context = new ArrayList<>();
        recentContextItemCallback = null;
    }

    public Discuss setRecentContextItemCallback(@Nullable Function<Action, Future<Void>> recentContextItemCallback) {
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
                "本次讨论的主题为：" + host.getDiscussTopic() + "\n\n" +
                        "相关背景如下：\n" + host.getDiscussIntroduction() + "\n\n" +
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
                .compose(over -> {
                    return Future.succeededFuture(context);
                });
    }

    private Future<Boolean> round() {
        return KeelAsyncKit.iterativelyCall(members, member -> {
                    return member.act(context)
                            .compose(resp -> {
                                Action x = new Action(member.getActorName(), resp);
                                context.add(x);
                                executeCallbackForRecentWord(x);
                                return Future.succeededFuture();
                            });
                })
                .compose(v -> {
                    return host.shouldStopDiscuss(context)
                            .compose(shouldStop -> {
                                host.setToStop(shouldStop);
                                if (!shouldStop && (host.getGuideWord() == null || host.getGuideWord().isBlank())) {
                                    return Future.succeededFuture(false);
                                }

                                return host.act(context)
                                        .compose(conclusion -> {
                                            if (conclusion != null) {
                                                Action x = new Action(host.getActorName(), conclusion);
                                                context.add(x);
                                                executeCallbackForRecentWord(x);
                                            }
                                            return Future.succeededFuture(shouldStop);
                                        });
                            });
                });
    }
}
