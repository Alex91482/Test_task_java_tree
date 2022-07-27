package com.example.demo.genetor.reactor;

import com.example.demo.entity.SavedEvent;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class MyProcessorImpl implements Processor<SavedEvent, SavedEvent> {

    private final Logger logger = LoggerFactory.getLogger(MyProcessorImpl.class);
    private final ArrayBlockingQueue<SavedEvent> myBlockingQueue = new ArrayBlockingQueue<>(100, true); //очередь событий
    private final Set<Subscriber<SavedEvent>> subscribers = new HashSet<>(); //список подписчиков
    private final AtomicLong counterEventRequest = new AtomicLong();

    @Override
    public void subscribe(Subscriber s) {
        subscribers.add(s);
        System.out.println("This is subscribe, subscriber = " + subscribers.size() + " queue size = " + myBlockingQueue.size());
    }

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(SavedEvent event) {
        System.out.println("This is onNext, subscriber = " + subscribers.size() + " queue size = " + myBlockingQueue.size());
        //myBlockingQueue.add(event);
        //notifySubscribers();
        System.out.println("This is onNext, subscriber = " + subscribers.size() + " queue size = " + myBlockingQueue.size());
    }

    @Override
    public void onError(Throwable t) {
        logger.error("an error occurred in runtime: {}", t.getMessage());
    }

    @Override
    public void onComplete() {
        myBlockingQueue.add(new SavedEvent().builder()
                        .id(-1L)
                        .occurredEvent("В очереде больше нет событий.")
                        .fillCoffeeTank(0)
                        .fillTheWaterTank(0)
                        .eventTime(null)
                        .build());
        notifySubscribers();
        System.out.println("This is onComplete, subscriber = " + subscribers.size() + " queue size = " + myBlockingQueue.size());
    }

    public void addEventInQueue(SavedEvent savedEvent){
        myBlockingQueue.add(savedEvent);
    }

    private void notifySubscribers(){
        //если список подпищиков не пуст
        //если очередь не пуста то отправить сообщение

        if(!myBlockingQueue.isEmpty()) {
            SavedEvent currentSavedEvent = myBlockingQueue.poll();
            sendEvent(currentSavedEvent);

        }else{
            sendEvent(new SavedEvent().builder()
                    .id(-1L)
                    .occurredEvent("В очереде больше нет событий.")
                    .fillCoffeeTank(0)
                    .fillTheWaterTank(0)
                    .eventTime(null)
                    .build());
        }
    }

    private void sendEvent(SavedEvent currentSavedEvent){
        //разослать сообщение всем подписчикам

        for (Subscriber<SavedEvent> subscriber : subscribers) {
            subscriber.onNext(currentSavedEvent);
        }
    }
}
