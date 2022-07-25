package com.example.demo.controller;

import com.example.demo.entity.SavedEvent;
import com.example.demo.service.CoffeeMachineService;
import com.example.demo.service.CoffeeMachineServiceImpl;
import com.example.demo.service.beverages.EnumBeverages;
import com.example.demo.util.idgenerator.IdGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.view.Rendering;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Controller
public class ViewController {

    private CoffeeMachineService coffeeMachineService;
    private CoffeeMachineServiceImpl coffeeMachineServiceImpl;

    public ViewController(CoffeeMachineService coffeeMachineService, CoffeeMachineServiceImpl coffeeMachineServiceImpl){
        this.coffeeMachineService = coffeeMachineService;
        this.coffeeMachineServiceImpl = coffeeMachineServiceImpl;
    }

    @RequestMapping(value = "/index1", method = RequestMethod.GET)
    public Mono<Rendering> getIndex(final Model model){

        //если направлять запросы на https а не на http то будет падать с ошибкой java.lang.IllegalArgumentException
        return Mono.just(Rendering.view("index1").build());
    }

    @GetMapping("/get-americano")
    public Mono<Rendering> getView(final Model model){
        //отправляем сообщения о приготовленных напитках
        SavedEvent se = new SavedEvent().builder()
                .id(new IdGenerator().getRandomId())
                .occurredEvent(EnumBeverages.Americano.toString())
                .eventTime(LocalDateTime.now())
                .fillCoffeeTank(1000)
                .fillTheWaterTank(1000)
                .build();
        return Mono.just(
                Rendering.view("index1")
                        .modelAttribute("strs",
                                new ReactiveDataDriverContextVariable(coffeeMachineService.test1(se), 1,1))
                        .build());
    }

    @GetMapping("/get-espresso")
    public Mono<Rendering> getView1(final Model model){
        //отправляем сообщения о приготовленных напитках
        SavedEvent se = new SavedEvent().builder()
                .id(new IdGenerator().getRandomId())
                .occurredEvent(EnumBeverages.Espresso.toString())
                .eventTime(LocalDateTime.now())
                .fillCoffeeTank(1000)
                .fillTheWaterTank(1000)
                .build();
        return Mono.just(
                Rendering.view("index1")
                        .modelAttribute("strs",
                                new ReactiveDataDriverContextVariable(coffeeMachineService.test1(se), 1,1))
                        .build());
    }

    @GetMapping("/get-doubleespresso")
    public Mono<Rendering> getView2(final Model model){
        //
        return Mono.just(
                Rendering.view("index1")
                        .modelAttribute("strs",
                                new ReactiveDataDriverContextVariable( coffeeMachineServiceImpl.getFlux(
                                        EnumBeverages.DoubleEspresso), 1,1))
                        .build());
    }
}
