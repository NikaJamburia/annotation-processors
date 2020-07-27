package com.nika.annotations.framework.event;

import java.util.ArrayList;
import java.util.List;

public class LazyServiceActivated implements Event {
    private final Object service;

    private static List<LazyServiceActivatedListener> subscribers = new ArrayList<>();
    public static void subscribe(LazyServiceActivatedListener listener) {
        subscribers.add(listener);
    }

    public LazyServiceActivated(Object service) {
        this.service = service;
    }

    public Object getService() {
        return service;
    }

    @Override
    public void publish() {
        subscribers.forEach(listener -> listener.lazyServiceActivated(service));
    }
}
