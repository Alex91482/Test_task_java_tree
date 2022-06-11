package com.example.demo.genetor;

import com.example.demo.entity.SavedEvent;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MySubscriber implements Subscriber<SavedEvent> {

    private final ArrayBlockingQueue<SavedEvent> myBlockingQueue = new ArrayBlockingQueue<>(100, true);
    private final AtomicInteger xz = new AtomicInteger();
    private final int take;
    private Subscription subscription;

    public MySubscriber(int take){ //определяет количество новостей которое готов принять пользователь
        this.take = take;
    }

    @Override
    public void onSubscribe(Subscription s) { //сообщение пред подпиской и далее подписка
        //...
        subscription = s;
        subscription.request(take); //посылаем запрос с количеством новостей которые готовы принять
    }

    @Override
    public void onNext(SavedEvent savedEvent) { //получаем от издателя
        myBlockingQueue.add(savedEvent); //offer ???
    }

    @Override
    public void onError(Throwable t) {
        //
    }

    @Override
    public void onComplete() {
        //
    }

    public Optional<SavedEvent> getEvent(){
        //смотрим есть ли непрочитанные сообщения у подписчика что бы запросить если нужно у издателя
        SavedEvent savedEvent = myBlockingQueue.poll();
        if(savedEvent != null){
            if(xz.decrementAndGet() == 0){
                subscription.request(take); //request способ запроса у издателя
                xz.set(take);
            }
            return Optional.of(savedEvent);
        }
        return Optional.empty();
    }
}
