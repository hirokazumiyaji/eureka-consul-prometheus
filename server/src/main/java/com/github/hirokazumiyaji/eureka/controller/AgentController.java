package com.github.hirokazumiyaji.eureka.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.hirokazumiyaji.eureka.model.Agent;
import com.github.hirokazumiyaji.eureka.model.AgentConfig;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AgentController {

    @GetMapping("/v1/agent/self")
    public Mono<Agent> self() {
        return Mono.just(new Agent(new AgentConfig("default")));
    }
}
