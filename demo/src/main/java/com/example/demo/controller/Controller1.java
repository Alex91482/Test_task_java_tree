package com.example.demo.controller;

import com.example.demo.entity.SavedEvent;
import com.example.demo.service.CoffeeMachineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.view.Rendering;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Controller
public class Controller1 {

    private CoffeeMachineService coffeeMachineService;

    public Controller1(CoffeeMachineService coffeeMachineService){
        this.coffeeMachineService = coffeeMachineService;
    }


    @GetMapping("/test_index_v1")
    public Mono<Rendering> getView(final Model model){
        //добавить вывод на страницу в очереде столько то заказов
        return Mono.just(
                Rendering.view("index1")
                        .modelAttribute("strs",
                                new ReactiveDataDriverContextVariable(coffeeMachineService.test1(), 1,1))
                        .build());
    }

    @RequestMapping(value = "/index1", method = RequestMethod.GET)
    public Mono<Rendering> getIndex(final Model model){

        //если направлять запросы на https а не на http то будет падать с ошибкой java.lang.IllegalArgumentException
        return Mono.just(Rendering.view("index1").build());
    }

    @GetMapping("/last")
    public Disposable ggg(){
        return coffeeMachineService.getLastEvent().subscribe(SavedEvent::toString);
    }
}
