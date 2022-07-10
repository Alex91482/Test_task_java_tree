package com.example.demo;

import com.example.demo.entity.SavedEvent;
import com.example.demo.genetor.classic.MyPublisher;
import com.example.demo.genetor.classic.MySubscriber;
import com.example.demo.util.idgenerator.IdGenerator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public class MyPublisherTest {

    @Test
    public void test1(){

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
}
