package com.github.hirokazumiyaji.eureka.service;

import com.github.hirokazumiyaji.eureka.model.CatalogService;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DiscoveryService {

    private final PeerAwareInstanceRegistry peerAwareInstanceRegistry;

    public Mono<Map<String, String[]>> getServiceNames(long waitMillis, long index) {
        Mono<Map<String, String[]>> serviceNames = Flux
            .fromStream(peerAwareInstanceRegistry.getApplications()
                .getRegisteredApplications()
                .stream())
            .collectMap(Application::getName, it -> new String[0]);
        return serviceNames;
    }

    public Mono<List<CatalogService>> getService(String name, long waitMillis, long index) {
        return Mono.empty();
    }
}
