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

    //создаем "горячего" издателя
    //public Flux<SavedEvent> queueCoffee= Flux.fromStream(() -> ).share();

    public Mono<SavedEvent> getLatestRecord(){
        return savedEventDAO.getTheLatestEntry();
    }

    public Mono<List<SavedEvent>> getByOccurredEvent(){
        return savedEventDAO.findByOccurredName("Coffee Machine start").collectList();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final ArrayBlockingQueue<String> myBlockingQueue = new ArrayBlockingQueue<>(100, true);
    private int counter = 0;

    private final Flux<String> myFlux = Flux
            .fromIterable(myBlockingQueue)          //каждый пользователь получит копию элементов из очереди
            .delayElements(Duration.ofSeconds(1))   //элементы перебераются с интервалом в 1 секунду
            ;


    public Flux<String> testAddFlux3(String str){
        // эмитация добавления заказа в очередь
        //возвращает поток с заказами
        counter++;
        System.out.println("Start a count " + counter + " " + str);
        myBlockingQueue.add(
                ">> Queue: " + LocalTime.now().toString() + ", counter: " + counter + ", thread: " + str
        );
        return myFlux;
        //вместо сообщения о завершении должно извлекатся событие из очереди
    }

    public void blockingQueuePool(){
        //поскольку при каждом вызове в очередь добавляется один заказ
        //то после извлечении заказа он должен быть удален из очереди
        myBlockingQueue.poll();
    }

    public Flux<String> test1(){
        //если заказы сделанны одновременно то пользователи оба увидят цепочку из двух заказов что есть ошибка
        return Flux.fromIterable(myBlockingQueue).delayElements(Duration.ofSeconds(1)).doOnComplete(this::blockingQueuePool);
    }
}
