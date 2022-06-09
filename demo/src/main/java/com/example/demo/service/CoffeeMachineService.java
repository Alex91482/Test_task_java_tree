package com.example.demo.service;

import com.example.demo.dao.SavedEventDAOImpl;
import com.example.demo.entity.SavedEvent;
import com.example.demo.service.beverages.AbstractCoffeeBeverages;
import com.example.demo.service.beverages.EnumBeverages;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;

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

    private Flux<SavedEvent> fx = Flux.fromIterable(myBlockingQueue)
            .delayElements(Duration.ofSeconds(10))
            .map(data ->{
                myBlockingQueue.poll();
                return data;
            })
            .repeat(); //когда поток заканчивается подписка остается, уверен что это жуткий говнокод но пока идей нет

    ConnectableFlux<SavedEvent> cf = fx.publish(); //поток теперь разадется на всех один


    public Flux<String> test1(SavedEvent savedEvent) {

        myBlockingQueue.add(savedEvent);
        cf.connect(); //запускаем на всех один поток публикации

        //добавить отписку при совпадении айди
        return cf
                .doOnNext(data -> {
                    if(data.getId() == savedEvent.getId()){
                        //как отписатся то???
                        System.out.println("Event " + data.getOccurredEvent());
                    }
                })
                .map(SavedEvent::getOccurredEvent);
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
