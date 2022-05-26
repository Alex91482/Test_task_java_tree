package com.example.demoReactiveWebFlux.controller.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

//@Configuration
public class RouterConfig {

    private TimeHandler timeHandler;

    //@Autowired
    public RouterConfig(TimeHandler timeHandler) {
        this.timeHandler = timeHandler;
    }


    //@Bean
    public RouterFunction<ServerResponse> timeRouter(){
        return route(GET("/times"), timeHandler::sendTimePerSec);
    }
}
