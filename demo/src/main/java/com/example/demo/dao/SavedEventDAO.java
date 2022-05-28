package com.example.demo.dao;


import com.example.demo.entity.SavedEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SavedEventDAO {

    void save(SavedEvent savedEvent);

    Mono<SavedEvent> findById(Long id);

    Mono<SavedEvent> getTheLatestEntry();

    Flux<SavedEvent> findAll();

    Flux<SavedEvent> findByOccurredName(String name);

    Mono<SavedEvent> update(SavedEvent savedEvent);

    Mono<Void> delete(Long id);
}
