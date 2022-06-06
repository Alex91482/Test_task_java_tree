package com.example.demo.service;

import com.example.demo.dao.SavedEventDAOImpl;
import com.example.demo.entity.SavedEvent;
import com.example.demo.service.beverages.AbstractCoffeeBeverages;
import com.example.demo.service.beverages.EnumBeverages;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class CoffeeMachineService {

    private SavedEventDAOImpl savedEventDAOImpl; //взаимодействие с бд
    private BeveragesCoffeeFactory beveragesCoffeeFactory; //фабрика по созданию напитков
    //private IdGenerator idGenerator; //генератор id для сущностей

    public CoffeeMachineService(SavedEventDAOImpl savedEventDAOImpl, BeveragesCoffeeFactory beveragesCoffeeFactory
                /*,IdGenerator idGenerator*/){
        this.savedEventDAOImpl = savedEventDAOImpl;
        this.beveragesCoffeeFactory = beveragesCoffeeFactory;
        //this.idGenerator = idGenerator;
    }

    //размер очереди ограничен 100 записями, далее они будут перезаписыватся
    private final ArrayBlockingQueue<SavedEvent> myBlockingQueue = new ArrayBlockingQueue<>(100, true);
    private final Flux<String> fx = Flux.fromIterable(myBlockingQueue)
            //.concatMap(SavedEvent::getOccurredEvent)
            .map(SavedEvent::getOccurredEvent)
            .delayElements(Duration.ofSeconds(15))
            .doOnComplete(myBlockingQueue::poll);

    public Flux<String> test1(SavedEvent savedEvent){
        //если заказы сделанны одновременно то пользователи оба увидят цепочку из двух заказов что есть ошибка
        //и вообще это все тянет на состояние гонки?

        myBlockingQueue.add(savedEvent);
        //myBlockingQueue.add(">> Performed: " + savedEvent.getOccurredEvent());

        //поскольку при каждом вызове в очередь добавляется один заказ
        //то после извлечении заказа он должен быть удален из очереди
        /*return Flux.fromIterable(myBlockingQueue)
                .delayElements(Duration.ofSeconds(15))
                .map(SavedEvent::getOccurredEvent)
                .doOnComplete(myBlockingQueue::poll);*/
        return fx;
    }

    public int getSizeQueue(){
        return myBlockingQueue.size();
    }

    public boolean areThereEnoughCoffeeIngredients(EnumBeverages typeBeverages){
        //достаточно ли ингредиентов для кофе
        //делаем запрос на последнюю сущьность и вычитаем из той воды что есть потребляемую воду и кофе потребляемое кофе
        //если хоть одно число стало меньше нуля то возвращаем фолз

        return getLatestRecord().subscribe(result -> {
                    stage1(result, typeBeverages);
                }).isDisposed();
    }

    private boolean stage1(SavedEvent lastSavedEvent, EnumBeverages typeBeverages){
        //проводим вычитания из текущих показаний хватит ли на напиток кофе и воды
        AbstractCoffeeBeverages beverages = beveragesCoffeeFactory.createCoffeeBeverages(typeBeverages);
        int waterLevel = lastSavedEvent.getFillTheWaterTank() - beverages.getWaterConsumption();
        int coffeeLevel = lastSavedEvent.getFillCoffeeTank() - beverages.getCoffeeConsumption();

        return waterLevel >= 0 && coffeeLevel >= 0;
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
