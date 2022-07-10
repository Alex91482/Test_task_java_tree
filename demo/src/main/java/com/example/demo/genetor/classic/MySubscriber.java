package com.example.demo.genetor.classic;

import com.example.demo.entity.SavedEvent;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MySubscriber implements Subscriber<SavedEvent> {

    //private final ArrayBlockingQueue<SavedEvent> myBlockingQueue = new ArrayBlockingQueue<>(100, true);
    private final AtomicInteger xz = new AtomicInteger();
    private int take = 1;
    private Subscription subscription;
    private SavedEvent savedEvent;

    public MySubscriber(int take, MyPublisher mp){ //определяет количество новостей которое готов принять пользователь
        this.take = take;
        mp.subscribe(this);
    }

    public MySubscriber(MyPublisher mp){
        mp.subscribe(this);
    }

    @Override
    public void onSubscribe(Subscription s) { //сообщение пред подпиской и далее подписка
        subscription = s;
        subscription.request(1);
    }

    @Override
    public void onNext(SavedEvent savedEvent) { //получаем от издателя
        this.savedEvent = savedEvent;
    }

    @Override
    public void onError(Throwable t) {
        //
    }

    @Override
    public void onComplete() {
        //
    }

    public SavedEvent getSavedEvent(){
        return savedEvent;
    }
}
