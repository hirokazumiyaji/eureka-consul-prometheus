package com.github.hirokazumiyaji.eureka.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.hirokazumiyaji.eureka.model.CatalogService;
import com.github.hirokazumiyaji.eureka.model.Item;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DiscoveryService {

    private static final List<String> STRINGS = new ArrayList<>();
    private static final String DEFAULT_MANAGEMENT_PORT = "28080";
    private static final String MANAGEMENT_PORT_META = "management.port";
    private final PeerAwareInstanceRegistry peerAwareInstanceRegistry;
    private final InstanceEventEmitter instanceEventEmitter;

    public Mono<Item<Map<String, List<String>>>> getServiceNames(long waitMillis, Long index) {
        final Map<String, List<String>> serviceNames = peerAwareInstanceRegistry
                .getApplications()
                .getRegisteredApplications()
                .stream()
                .collect(Collectors.toMap(Application::getName, it -> STRINGS));
        final Long lastEmitted = instanceEventEmitter.getLastEmitted();
        if (!lastEmitted.equals(index)) {
            return Mono.just(new Item<>(serviceNames, lastEmitted));
        }

        return instanceEventEmitter.getTotalIndex(waitMillis)
                                   .next()
                                   .map(it -> new Item<>(serviceNames, it.longValue()));
    }

    public Mono<Item<List<CatalogService>>> getService(String name, long waitMillis, Long index) {
        final List<CatalogService> services = Optional
                .ofNullable(peerAwareInstanceRegistry.getApplication(name))
                .orElse(new Application())
                .getInstances()
                .stream()
                .map(DiscoveryService::convert)
                .collect(Collectors.toList());
        final Long lastEmitted = instanceEventEmitter.getLastEmittedOfApp(name);
        if (!lastEmitted.equals(index)) {
            return Mono.just(new Item<>(services, lastEmitted));
        }
        return instanceEventEmitter.getIndexOfApp(name, waitMillis)
                                   .next()
                                   .map(it -> new Item<>(services, it.longValue()));
    }

    private static CatalogService convert(InstanceInfo instanceInfo) {
        final String address = instanceInfo.getIPAddr();
        CatalogService service = new CatalogService();
        service.setId(instanceInfo.getId());
        service.setServiceID(instanceInfo.getId());
        service.setNode(instanceInfo.getAppName());
        service.setServiceName(instanceInfo.getAppName());
        service.setAddress(address);
        service.setDatacenter("default");
        service.setServiceAddress(address);
        service.setServicePort(
                Integer.valueOf(
                        instanceInfo.getMetadata()
                                    .getOrDefault(MANAGEMENT_PORT_META, DEFAULT_MANAGEMENT_PORT))
                       .intValue());
        Map<String, String> meta = instanceInfo
                .getMetadata()
                .entrySet()
                .stream()
                .filter(it -> !it.getKey().equals(MANAGEMENT_PORT_META))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        meta.putIfAbsent("group", instanceInfo.getAppGroupName());
        meta.putIfAbsent("hostname", instanceInfo.getHostName());
        service.setServiceMeta(meta);
        return service;
    }
}
