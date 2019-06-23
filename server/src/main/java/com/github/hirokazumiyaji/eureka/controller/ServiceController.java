package com.github.hirokazumiyaji.eureka.controller;

import com.github.hirokazumiyaji.eureka.model.CatalogService;
import com.github.hirokazumiyaji.eureka.service.DiscoveryService;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ServiceController {

    private static final String CONSUL_IDX_HEADER = "X-Consul-Index";
    private static final String QUERY_PARAM_WAIT = "wait";
    private static final String QUERY_PARAM_INDEX = "index";
    private static final Pattern WAIT_PATTERN = Pattern.compile("(\\d*)(m|s|ms|h)");
    private static final Random RANDOM = new Random();

    private final DiscoveryService discoveryService;

    @GetMapping("/v1/catalog/service")
    public Mono<Map<String, String[]>> getServiceNames(
        @RequestParam(QUERY_PARAM_WAIT) String wait,
        @RequestParam(QUERY_PARAM_INDEX) long index
    ) {
        long millis = getWaitMillis(wait);
        return discoveryService.getServiceNames(millis, index);
    }

    @GetMapping("/v1/catalog/service/{name}")
    public Mono<List<CatalogService>> getService(
        @PathVariable String name,
        @RequestParam(QUERY_PARAM_WAIT) String wait,
        @RequestParam(QUERY_PARAM_INDEX) long index
    ) {
        long millis = getWaitMillis(wait);
        return discoveryService.getService(name, millis, index);
    }

    private long getWaitMillis(String wait) {
        long millis = TimeUnit.MINUTES.toMillis(5);
        if (wait != null) {
            Matcher matcher = WAIT_PATTERN.matcher(wait);
            if (matcher.matches()) {
                long value = Long.parseLong(matcher.group(1));
                TimeUnit timeUnit = parseTimeUnit(matcher.group(2));
                millis = timeUnit.toMillis(value);
            } else {
                throw new IllegalArgumentException("Invalid wait pattern");
            }
        }
        return millis + RANDOM.nextInt(((int) millis / 16) + 1);
    }

    private TimeUnit parseTimeUnit(String unit) {
        switch (unit) {
            case "h":
                return TimeUnit.HOURS;
            case "m":
                return TimeUnit.MINUTES;
            case "s":
                return TimeUnit.SECONDS;
            case "ms":
                return TimeUnit.MILLISECONDS;
            default:
                throw new IllegalArgumentException("No valid time unit");
        }
    }
}
