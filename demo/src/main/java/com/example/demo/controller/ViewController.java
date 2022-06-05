package com.example.demo.controller;

import com.example.demo.service.CoffeeMachineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.view.Rendering;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

@Controller
public class ViewController {

    private CoffeeMachineService coffeeMachineService;

    public ViewController(CoffeeMachineService coffeeMachineService){
        this.coffeeMachineService = coffeeMachineService;
    }

    @RequestMapping(value = "/index1", method = RequestMethod.GET)
    public Mono<Rendering> getIndex(final Model model){

        //если направлять запросы на https а не на http то будет падать с ошибкой java.lang.IllegalArgumentException
        return Mono.just(Rendering.view("index1").build());
    }

    @GetMapping("/view_v1")
    public Mono<Rendering> getView(final Model model){
        //отправляем сообщения о приготовленных напитках
        return Mono.just(
                Rendering.view("index1")
                        .modelAttribute("strs",
                                new ReactiveDataDriverContextVariable(coffeeMachineService.test1(), 1,1))
                        .build());
    }
}
