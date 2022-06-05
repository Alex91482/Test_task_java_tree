package com.example.demo.controller;

import com.example.demo.entity.SavedEvent;
import com.example.demo.service.CoffeeMachineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class CoffeeRestController {

    private CoffeeMachineService coffeeMachineService;

    public CoffeeRestController(CoffeeMachineService coffeeMachineService){
        this.coffeeMachineService = coffeeMachineService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testMethod(){
        return new ResponseEntity<>("test", HttpStatus.OK);
    }

    @GetMapping("/get-latest-entry")
    public Mono<SavedEvent> getLatestEntry(){
        return coffeeMachineService.getLatestRecord();
    }

    @PostMapping("/get-record-start-machine")
    public Mono<List<SavedEvent>> getAllRecord(){
        return coffeeMachineService.getByOccurredEvent();
    }
}
