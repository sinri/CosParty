package io.github.sinri.CosParty.actor;

public record Action(String actorName, String message) {
    @Override
    public String toString() {
        return "ContextItem{" + actorName + ":" + message + '}';
    }
}
