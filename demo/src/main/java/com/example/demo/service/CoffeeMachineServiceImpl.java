package com.example.demo.service;

import com.example.demo.dao.SavedEventDAOImpl;
import com.example.demo.entity.SavedEvent;
import com.example.demo.service.beverages.AbstractCoffeeBeverages;
import com.example.demo.service.beverages.EnumBeverages;
import com.example.demo.util.idgenerator.IdGenerator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CoffeeMachineServiceImpl {

    private SavedEventDAOImpl savedEventDAOImpl; //взаимодействие с бд
    private BeveragesCoffeeFactory beveragesCoffeeFactory; //фабрика по созданию напитков
    private IdGenerator idGenerator; //генератор id для сущностей

    public CoffeeMachineServiceImpl(SavedEventDAOImpl savedEventDAOImpl, BeveragesCoffeeFactory beveragesCoffeeFactory
            ,IdGenerator idGenerator){
        this.savedEventDAOImpl = savedEventDAOImpl;
        this.beveragesCoffeeFactory = beveragesCoffeeFactory;
        this.idGenerator = idGenerator;
    }

    //размер очереди ограничен 100 записями, далее они будут перезаписыватся
    private final ArrayBlockingQueue<SavedEvent> myBlockingQueue = new ArrayBlockingQueue<>(100, true);

    private Flux<SavedEvent> myPublisher = Flux.from(event -> {
        event.onNext(getEvent());
        event.onComplete();
    });

    private final ConnectableFlux<SavedEvent> bigBrother = myPublisher.publish(); //поток теперь разадается на всех один

    private SavedEvent getEvent(){
        if(!myBlockingQueue.isEmpty()) {
            try {
                Thread.sleep(7000);
                return myBlockingQueue.poll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new SavedEvent().builder()
                .id(idGenerator.getRandomId())
                .occurredEvent("Error")
                .fillCoffeeTank(0)
                .fillTheWaterTank(0)
                .eventTime(LocalDateTime.now())
                .build(); //событие ошибка
    }

    public Flux<String> getFlux(EnumBeverages typeBeverages) {

        areThereEnoughCoffeeIngredients(typeBeverages);
        bigBrother.connect(); //запускаем на всех один поток публикации
        return bigBrother.map(SavedEvent::getOccurredEvent);
    }

    private void areThereEnoughCoffeeIngredients(EnumBeverages typeBeverages){
        //
        savedEventDAOImpl.getTheLatestEntry().map(event -> {
            getEventValidation(event, typeBeverages);
            System.out.println(getSizeQueue());
            return event;
        }).subscribe().isDisposed();
    }

    private void getEventValidation(SavedEvent lastSavedEvent, EnumBeverages typeBeverages){
        AbstractCoffeeBeverages beverages = beveragesCoffeeFactory.createCoffeeBeverages(typeBeverages);
        int waterLevel = lastSavedEvent.getFillTheWaterTank() - beverages.getWaterConsumption();
        int coffeeLevel = lastSavedEvent.getFillCoffeeTank() - beverages.getCoffeeConsumption();
        if(waterLevel >= 0 && coffeeLevel >= 0){
            SavedEvent se = new SavedEvent().builder()
                    .id(idGenerator.getRandomId())
                    .occurredEvent(typeBeverages.toString())
                    .fillCoffeeTank(coffeeLevel)
                    .fillTheWaterTank(waterLevel)
                    .eventTime(LocalDateTime.now())
                    .build();
            savedEventDAOImpl.save(se);
            myBlockingQueue.add(se);
        }else {
            SavedEvent se = new SavedEvent().builder()
                    .id(idGenerator.getRandomId())
                    .occurredEvent("Не достаточно ингредиентов")
                    .fillCoffeeTank(lastSavedEvent.getFillCoffeeTank())
                    .fillTheWaterTank(lastSavedEvent.getFillTheWaterTank())
                    .eventTime(LocalDateTime.now())
                    .build();
            savedEventDAOImpl.save(se);
            myBlockingQueue.add(se);
        }
    }

    public Mono<List<SavedEvent>> getByOccurredEvent(){
        //получить все события "Coffee Machine start" используется псрото для примера
        return savedEventDAOImpl.findByOccurredName("Coffee Machine start").collectList();
    }

    public int getSizeQueue(){
        return myBlockingQueue.size();
    }
}
