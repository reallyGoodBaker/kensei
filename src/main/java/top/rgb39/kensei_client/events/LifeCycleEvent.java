package top.rgb39.kensei_client.events;

import top.rgb39.ecs.arch.Event;

public record LifeCycleEvent(Type type) implements Event {

    public enum Type {
        CLIENT_END,
    }
}
