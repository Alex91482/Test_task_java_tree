package com.example.demo.service;

import com.example.demo.dao.SavedEventDAOImpl;
import com.example.demo.entity.SavedEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class CoffeeMachineService {

    private SavedEventDAOImpl savedEventDAOImpl;

    public CoffeeMachineService(SavedEventDAOImpl savedEventDAOImpl){
        this.savedEventDAOImpl = savedEventDAOImpl;
    }

    //размер очереди ограничен 100 записями, далее они будут перезаписыватся
    private final ArrayBlockingQueue<String> myBlockingQueue = new ArrayBlockingQueue<>(100, true);

    public Flux<String> test1(){
        //если заказы сделанны одновременно то пользователи оба увидят цепочку из двух заказов что есть ошибка
        //и вообще это все тянет на состояние гонки
        System.out.println("Start ");
        myBlockingQueue.add(">> Queue: " + LocalTime.now().toString());

        //поскольку при каждом вызове в очередь добавляется один заказ
        //то после извлечении заказа он должен быть удален из очереди
        return Flux.fromIterable(myBlockingQueue)
                .delayElements(Duration.ofSeconds(15))
                .doOnComplete(myBlockingQueue::poll);
    }

    public int getSizeQueue(){
        return myBlockingQueue.size();
    }

    public Mono<SavedEvent> getLatestRecord(){
        //получить последнее событие
        return savedEventDAOImpl.getTheLatestEntry();
    }

    public Mono<List<SavedEvent>> getByOccurredEvent(){
        //получить все события "Coffee Machine start" используется прото для примера
        return savedEventDAOImpl.findByOccurredName("Coffee Machine start").collectList();
    }
}
