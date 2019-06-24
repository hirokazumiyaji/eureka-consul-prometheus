package com.github.hirokazumiyaji.eureka.service;

import org.springframework.cloud.netflix.eureka.server.InstanceRegistry;
import org.springframework.cloud.netflix.eureka.server.InstanceRegistryProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;

@Primary
@Component
public class EurekaInstanceRegistry extends InstanceRegistry {

    private final InstanceEventEmitter instanceEventEmitter;

    public EurekaInstanceRegistry(
            EurekaServerConfig serverConfig,
            EurekaClientConfig clientConfig,
            ServerCodecs serverCodecs,
            EurekaClient eurekaClient,
            InstanceRegistryProperties instanceRegistryProperties,
            InstanceEventEmitter instanceEventEmitter
    ) {
        super(serverConfig,
              clientConfig,
              serverCodecs,
              eurekaClient,
              instanceRegistryProperties.getExpectedNumberOfClientsSendingRenews(),
              instanceRegistryProperties.getDefaultOpenForTrafficCount());
        this.instanceEventEmitter = instanceEventEmitter;
    }

    @Override
    public void register(InstanceInfo info, boolean isReplication) {
        super.register(info, isReplication);
        instanceEventEmitter.publish(info.getAppName(), System.currentTimeMillis());
    }

    @Override
    public void register(InstanceInfo info, int leaseDuration, boolean isReplication) {
        super.register(info, leaseDuration, isReplication);
        instanceEventEmitter.publish(info.getAppName(), System.currentTimeMillis());
    }

    @Override
    public boolean cancel(String appName, String serverId, boolean isReplication) {
        boolean result = super.cancel(appName, serverId, isReplication);
        instanceEventEmitter.publish(appName, System.currentTimeMillis());
        return result;
    }
}
