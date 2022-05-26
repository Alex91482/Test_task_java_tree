package com.example.demoReactiveWebFlux.controller;

import com.example.demoReactiveWebFlux.service.CoffeeMachineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

@Controller
public class TestController1 {

    private CoffeeMachineService coffeeMachineService;

    public TestController1(CoffeeMachineService coffeeMachineService){
        this.coffeeMachineService = coffeeMachineService;
    }

    @RequestMapping("/index")
    public String view(final Model model){

        model.addAttribute("strs",
                new ReactiveDataDriverContextVariable(coffeeMachineService.test1(), 1,1));
        return "index1";
    }

    @RequestMapping(value = "/test_index", method = RequestMethod.GET)
    public String viewTest(){
        return "index1";
    }
}
