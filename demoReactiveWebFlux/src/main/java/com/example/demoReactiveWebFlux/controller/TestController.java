package com.example.demoReactiveWebFlux.controller;

import com.example.demoReactiveWebFlux.service.CoffeeMachineService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class TestController {

    private CoffeeMachineService coffeeMachineService;

    public TestController(CoffeeMachineService coffeeMachineService){
        this.coffeeMachineService = coffeeMachineService;
    }

    @GetMapping(path = "/test1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> test1(){
        return coffeeMachineService.test1();
    }

    //метод должен запрашивать количество ингредиентов из бд и если их не достаточно возвращать сообщение об этом
    //если ингредиентов достаточно должнен возвращатся поток событий (поток выполнения заказов)
    //можно конечно без потока просто сообщение пользователю кидать что заказ выполнен
}
