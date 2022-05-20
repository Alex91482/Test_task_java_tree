package com.example.demoReactiveWebFlux.service;

import com.example.demoReactiveWebFlux.dao.SavedEventDAOImpl;
import com.example.demoReactiveWebFlux.entity.SavedEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CoffeeMachineService {

    private SavedEventDAOImpl savedEventDAO;

    public CoffeeMachineService(SavedEventDAOImpl savedEventDAO){
        this.savedEventDAO = savedEventDAO;
    }

    //создаем "горячего" издателя
    //public Flux<SavedEvent> queueCoffee= Flux.fromStream(() -> ).share();

    public Mono<SavedEvent> getLatestRecord(){
        return savedEventDAO.getTheLatestEntry();
    }

    public Mono<List<SavedEvent>> getByOccurredEvent(){
        return savedEventDAO.findByOccurredName("Coffee Machine start").collectList();
    }

}
