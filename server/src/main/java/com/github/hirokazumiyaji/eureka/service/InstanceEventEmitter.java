package com.github.hirokazumiyaji.eureka.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@Component
public class InstanceEventEmitter {

    private static final long INITIAL_VALUE = 1L;
    private final EmitterProcessor<Change> emitterProcessor = EmitterProcessor.create();
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

    public void publish(String name, long timestamp) {
        final Change change = new Change(name, timestamp);
        counters.computeIfAbsent(name, it -> new AtomicLong(INITIAL_VALUE)).set(timestamp);
        emitterProcessor.onNext(change);
    }

    public Flux<Long> getIndexOfApp(String name, long millis) {
        return emitterProcessor.filter(it -> it.getName().equals(name))
                               .timeout(Duration.ofMillis(millis))
                               .onErrorResume(it -> Flux.just(mapTimeoutToServiceChange(name, it)))
                               .map(it -> getLastEmittedOfApp(name));

    }

    public Flux<Long> getTotalIndex(long millis) {
        return emitterProcessor
                .timeout(Duration.ofMillis(millis))
                .onErrorResume(it -> Flux.just(mapTimeoutToServiceChange("", it)))
                .map(it -> getLastEmitted());
    }

    public Long getLastEmitted() {
        return counters.values()
                       .stream()
                       .mapToLong(AtomicLong::get)
                       .max()
                       .orElse(INITIAL_VALUE);
    }

    public Long getLastEmittedOfApp(String name) {
        return counters.getOrDefault(name, new AtomicLong(INITIAL_VALUE))
                       .longValue();
    }

    public void reset() {
        counters.clear();
    }

    private static Change mapTimeoutToServiceChange(String name, Throwable cause) {
        if (cause instanceof TimeoutException) {
            return new Change(name, -1);
        }
        throw new RuntimeException(cause.getMessage(), cause);
    }

    @AllArgsConstructor
    @Data
    private static class Change {
        private String name;
        private long timestamp;
    }
}
