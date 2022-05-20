package com.example.demoReactiveWebFlux.controller;

import com.example.demoReactiveWebFlux.entity.SavedEvent;
import com.example.demoReactiveWebFlux.service.CoffeeMachineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;


@RestController
//@RequestMapping("/rest")
public class Controller {

    private CoffeeMachineService coffeeMachineService;

    public Controller(CoffeeMachineService coffeeMachineService){
        this.coffeeMachineService = coffeeMachineService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testMethod(){
        return new ResponseEntity<>("test", HttpStatus.OK);
    }

    @PostMapping("/get-latest-entry")
    public Mono<SavedEvent> getLatestEntry(){
        return coffeeMachineService.getLatestRecord();
    }

    @PostMapping("/get-record-start-machine")
    public Mono<List<SavedEvent>> getAllRecord(){
        return coffeeMachineService.getByOccurredEvent();
    }


    @GetMapping(path = "/test-flux1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux streamFlux() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent. builder()
                        .data("Flux - " + LocalTime.now().toString())
                        .build()
                );
    }

    @GetMapping(path = "/test-flux2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux1() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Flux - " + LocalTime.now().toString());
    }

    @GetMapping("/test-flux3")
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String> builder()
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("SSE - " + LocalTime.now().toString())
                        .build());
    }

    @GetMapping("/test-mono1")
    public Mono<String> monoStreamEvents(){
        final Flux<Object> fs = Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String> builder()
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("SSE - " + LocalTime.now().toString())
                        .build());
        return fs
                //.takeUntilOther()
                .buffer(3)
                .then(Mono.just("f"));
    }

    /*@RequestMapping("/test-mono2")
    public Mono<String> getMonoTe(final Model model){
        final Flux<Object> fs = Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String> builder()
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("SSE - " + LocalTime.now().toString())
                        .build());
        return fs.collectList()
                .doOnNext(list -> model.addAttribute("xz", list))
                .then(Mono.just("freemarker/test-mono2"));
    }*/

    /*@GetMapping("/push-create")
    public String pushCreate(){
        return Flux.push(data -> );
    }*/

}
