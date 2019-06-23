package com.github.hirokazumiyaji.eureka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.flogger.FluentLogger;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;
import org.springframework.cloud.netflix.eureka.server.InstanceRegistryProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class InstanceRegistry extends
    org.springframework.cloud.netflix.eureka.server.InstanceRegistry {

    private static final FluentLogger log = FluentLogger.forEnclosingClass();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public InstanceRegistry(
        EurekaServerConfig serverConfig,
        EurekaClientConfig clientConfig,
        ServerCodecs serverCodecs,
        EurekaClient eurekaClient,
        InstanceRegistryProperties instanceRegistryProperties
    ) {
        super(serverConfig,
            clientConfig,
            serverCodecs,
            eurekaClient,
            instanceRegistryProperties.getExpectedNumberOfClientsSendingRenews(),
            instanceRegistryProperties.getDefaultOpenForTrafficCount());
    }

    @Override
    public void register(InstanceInfo info, boolean isReplication) {
        super.register(info, isReplication);
        try {
            log.atInfo().log(
                "register\tinstance info: %s\tis replication: %s",
                objectMapper.writeValueAsString(info),
                isReplication);
        } catch (JsonProcessingException e) {
            log.atWarning().withCause(e).log();
        }
    }

    @Override
    public void register(InstanceInfo info, int leaseDuration, boolean isReplication) {
        super.register(info, leaseDuration, isReplication);
        try {
            log.atInfo().log(
                "register\tinstance info: %s\tlease duration: %s\tis replication: %s",
                objectMapper.writeValueAsString(info),
                leaseDuration,
                isReplication);
        } catch (JsonProcessingException e) {
            log.atWarning().withCause(e).log();
        }
    }

    @Override
    public boolean cancel(String appName, String serverId, boolean isReplication) {
        log.atInfo().log(
            "register\tapp name: %s\tserver id: %s\tis replication: %s",
            appName,
            serverId,
            isReplication);
        return super.cancel(appName, serverId, isReplication);
    }
}
