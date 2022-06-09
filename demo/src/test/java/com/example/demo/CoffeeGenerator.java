package com.example.demo;

import com.example.demo.entity.SavedEvent;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;


public class CoffeeGenerator /*implements SynchronousSink*/ {

    private final int intervalSec = 15;

    //размер очереди ограничен 100 записями, далее они будут перезаписыватся
    private final ArrayBlockingQueue<SavedEvent> myBlockingQueueObserver = new ArrayBlockingQueue<>(100, true);
    private final ArrayBlockingQueue<SavedEvent> myBlockingQueueSubscriber = new ArrayBlockingQueue<>(100, true);

    public void onNext(SavedEvent savedEvent){
        myBlockingQueueObserver.add(savedEvent);
    }

    Flux<String> fx = Flux.fromIterable(myBlockingQueueObserver)
            .map(SavedEvent::getOccurredEvent)
            .delayElements(Duration.ofSeconds(3))
            .doOnComplete(myBlockingQueueObserver::poll);

    ConnectableFlux<String> cf = fx.publish(); //хз как это работает, по идее поток теперь разадется на всех один

    public static void main(String...args) throws InterruptedException {

        CoffeeGenerator coffeeGenerator = new CoffeeGenerator();
        coffeeGenerator.onNext(new SavedEvent().builder()
                        .id(1L)
                        .occurredEvent("test")
                        .eventTime(LocalDateTime.now())
                        .fillTheWaterTank(10)
                        .fillCoffeeTank(10)
                        .build()
        );
        coffeeGenerator.cf.connect();
        coffeeGenerator.cf.subscribe(System.out::println);

        Thread.sleep(10000);

    }

}
