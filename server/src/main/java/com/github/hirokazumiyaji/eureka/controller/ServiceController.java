package com.github.hirokazumiyaji.eureka.controller;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.hirokazumiyaji.eureka.model.CatalogService;
import com.github.hirokazumiyaji.eureka.service.DiscoveryService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ServiceController {

    private static final String CONSUL_IDX_HEADER = "X-Consul-Index";
    private static final String QUERY_PARAM_WAIT = "wait";
    private static final String QUERY_PARAM_INDEX = "index";
    private static final Pattern WAIT_PATTERN = Pattern.compile("(\\d*)(m|s|ms|h)");
    private static final Random RANDOM = new Random();

    private final DiscoveryService discoveryService;

    @GetMapping("/v1/catalog/services")
    public Mono<ResponseEntity<Map<String, List<String>>>> getServiceNames(
            @RequestParam(value = QUERY_PARAM_WAIT, required = false) String wait,
            @RequestParam(value = QUERY_PARAM_INDEX, required = false) Long index
    ) {
        long millis = getWaitMillis(wait);
        return discoveryService
                .getServiceNames(millis, index)
                .map(it -> new ResponseEntity<>(it.getItem(),
                                                headers(it.getIndex()),
                                                HttpStatus.OK));
    }

    @GetMapping("/v1/catalog/service/{name}")
    public Mono<ResponseEntity<List<CatalogService>>> getService(
            @PathVariable String name,
            @RequestParam(value = QUERY_PARAM_WAIT, required = false) String wait,
            @RequestParam(value = QUERY_PARAM_INDEX, required = false) Long index
    ) {
        long millis = getWaitMillis(wait);
        return discoveryService
                .getService(name, millis, index)
                .map(it -> new ResponseEntity<>(it.getItem(),
                                                headers(it.getIndex()),
                                                HttpStatus.OK));
    }

    private static HttpHeaders headers(long index) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONSUL_IDX_HEADER, String.valueOf(index));
        return headers;
    }

    private static long getWaitMillis(String wait) {
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
        return millis + RANDOM.nextInt((int) millis / 16 + 1);
    }

    private static TimeUnit parseTimeUnit(String unit) {
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
