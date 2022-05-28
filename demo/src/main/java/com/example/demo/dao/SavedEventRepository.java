package com.example.demo.dao;


import com.example.demo.entity.SavedEvent;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SavedEventRepository extends ReactiveMongoRepository<SavedEvent, Long> {

    @Query("{ 'eventTime': { \"$slice\" : -1 } }")
    Mono<SavedEvent> getTheLatestEntry();               //получить последнюю запись из бд

    Flux<SavedEvent> findByOccurredEvent(String name);  //получить события по названию
}
