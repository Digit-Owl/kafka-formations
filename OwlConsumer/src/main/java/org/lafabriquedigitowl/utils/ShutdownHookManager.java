package org.lafabriquedigitowl.utils;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class ShutdownHookManager {
    private static final ShutdownHookManager SHUTDOWN_HOOK_MANAGER = new ShutdownHookManager();

    private final Set<Hook> hooks = Collections.synchronizedSet(new HashSet<>());

    private final AtomicBoolean shutdownInProgress = new AtomicBoolean(false);

    private ShutdownHookManager() {
        //Never instantiate
    }

    static {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    SHUTDOWN_HOOK_MANAGER.shutdownInProgress.set(true);
                    log.info("Shutdown detected...");
                    for (Runnable runnable : SHUTDOWN_HOOK_MANAGER.getShutdownhookInOrderOfPriority()) {
                        try {
                            runnable.run();
                        } catch (Exception exception) {
                            log.error("Shutdownhook {} failed :: {}", runnable.getClass().getSimpleName(), exception);
                        }
                    }
                })
        );
    }

    public static ShutdownHookManager get() {
        return SHUTDOWN_HOOK_MANAGER;
    }

    public void addShutdownHook(Runnable runnable, int priority) {
        if (runnable == null) {
            throw new IllegalArgumentException("Shutdown hook runnable cannot be null");
        }
        if (shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add new hook");
        }
        hooks.add(new Hook(runnable, priority));
    }

    public boolean removeShutdownHook(Runnable runnable) {
        if (shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add new hook");
        }
        return hooks.remove(new Hook(runnable, 0));
    }

    public boolean hasShutdownHook(Runnable runnable) {
        return hooks.contains(new Hook(runnable, 0));
    }

    public List<Runnable> getShutdownhookInOrderOfPriority() {
        List<Hook> list;
        synchronized (SHUTDOWN_HOOK_MANAGER.hooks) {
            list = new ArrayList<>(SHUTDOWN_HOOK_MANAGER.hooks);
        }
        list.sort((o1, o2) -> o2.priority - o1.priority);
        List<Runnable> runnables = new ArrayList<>();
        for (Hook hook : list) {
            runnables.add(hook.hook);
        }
        return runnables;
    }

    @AllArgsConstructor
    private static class Hook {
        Runnable hook;
        int priority;

        @Override
        public int hashCode() {
            return hook.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            boolean eq = false;
            if (obj instanceof Hook) {
                eq = (hook == ((Hook) obj).hook);
            }
            return eq;
        }
    }
}
