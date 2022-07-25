package com.example.demo;

import com.example.demo.entity.SavedEvent;
import com.example.demo.genetor.classic.MyPublisher;
import com.example.demo.genetor.classic.MySubscriber;
import com.example.demo.util.idgenerator.IdGenerator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public class MyPublisherTest {

    @Test
    public void test1(){ //работает

        IdGenerator g = new IdGenerator();
        MyPublisher myPublisher = new MyPublisher();
        MySubscriber mySubscriber = new MySubscriber(myPublisher);

        myPublisher.setSaveEventInQueue(new SavedEvent().builder()
                .id(g.getRandomId())
                .occurredEvent("Coffee Machine start")
                .eventTime(LocalDateTime.now())
                .fillTheWaterTank(1000)
                .fillCoffeeTank(1000)
                .build());

        SavedEvent savedEvent = mySubscriber.getSavedEvent();

        assert savedEvent.getOccurredEvent().equals("Coffee Machine start");
    }

    @Test
    public void test2(){ //полная шляпа не работает
        MyPublisher myPublisher = new MyPublisher();
        IdGenerator g = new IdGenerator();

        myPublisher.setSaveEventInQueue(new SavedEvent().builder()
                .id(g.getRandomId())
                .occurredEvent("Coffee Machine start")
                .eventTime(LocalDateTime.now())
                .fillTheWaterTank(1000)
                .fillCoffeeTank(1000)
                .build());

        Flux<SavedEvent> fl = Flux.from(myPublisher);

        fl.subscribe(
                savedEvent -> {
                    System.out.println("sub");
                    System.out.println(savedEvent.getOccurredEvent());
                    //assert savedEvent.getOccurredEvent().equals("Coffee Machine start");
                }
        );
    }

    @Test
    public void test3(){ //работает
        IdGenerator g = new IdGenerator();

        SavedEvent se =
                new SavedEvent().builder()
                        .id(g.getRandomId())
                        .occurredEvent("Coffee Machine start")
                        .eventTime(LocalDateTime.now())
                        .fillTheWaterTank(1000)
                        .fillCoffeeTank(1000)
                        .build();

        Flux<SavedEvent> fl = Flux.from(pub -> {
            pub.onNext(se);
            pub.onComplete();
        });

        fl.subscribe(
                savedEvent -> {
                    System.out.println(savedEvent.getOccurredEvent());
                }
        );

        //assert savedEvent.getOccurredEvent().equals("Coffee Machine start");
    }
}
