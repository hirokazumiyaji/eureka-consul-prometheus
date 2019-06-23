package com.github.hirokazumiyaji.eureka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CatalogService {

    @JsonProperty("ID")
    String id;
    @JsonProperty("Node")
    String node;
    @JsonProperty("Address")
    String address;
    @JsonProperty("Datacenter")
    String datacenter;
    @JsonProperty("TaggedAddress")
    Map<String, String> taggedAddresses;
    @JsonProperty("NodeMeta")
    Map<String, String> nodeMeta;
    @JsonProperty("ServiceID")
    String serviceID;
    @JsonProperty("ServiceName")
    String serviceName;
    @JsonProperty("ServiceAddress")
    String serviceAddress;
    @JsonProperty("ServiceTags")
    List<String> serviceTags;
    @JsonProperty("ServiceMeta")
    Map<String, String> serviceMeta;
    @JsonProperty("ServicePort")
    int servicePort;
    @JsonProperty("ServiceEnableTagOverride")
    boolean serviceEnableTagOverride;
}
