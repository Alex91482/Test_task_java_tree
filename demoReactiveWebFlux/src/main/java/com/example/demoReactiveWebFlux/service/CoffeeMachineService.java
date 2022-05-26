package com.example.demoReactiveWebFlux.service;

import com.example.demoReactiveWebFlux.dao.SavedEventDAOImpl;
import com.example.demoReactiveWebFlux.entity.SavedEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class CoffeeMachineService {

    private SavedEventDAOImpl savedEventDAO;

    public CoffeeMachineService(SavedEventDAOImpl savedEventDAO){
        this.savedEventDAO = savedEventDAO;
    }

    public Mono<SavedEvent> getLatestRecord(){
        return savedEventDAO.getTheLatestEntry();
    }

    public Mono<List<SavedEvent>> getByOccurredEvent(){
        return savedEventDAO.findByOccurredName("Coffee Machine start").collectList();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

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
}
