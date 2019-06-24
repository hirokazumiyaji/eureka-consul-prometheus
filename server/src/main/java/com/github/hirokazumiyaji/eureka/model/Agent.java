package com.github.hirokazumiyaji.eureka.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Agent {
    @JsonProperty("Config")
    AgentConfig config;
}
