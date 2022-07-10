package com.example.demo.genetor.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;



public class MySubscriberImpl<T> extends BaseSubscriber<T> {

    public void hookOnSubscribe(Subscription subscription){
        request(1);
    }

    public void hookOnNext(T value){
        request(1);
    }
}
