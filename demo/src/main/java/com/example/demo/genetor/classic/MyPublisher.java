package com.example.demo.genetor.classic;

import com.example.demo.entity.SavedEvent;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class MyPublisher implements Publisher<SavedEvent>, Subscription {

    private final ArrayBlockingQueue<SavedEvent> myBlockingQueue = new ArrayBlockingQueue<>(100, true); //очередь событий
    private final Set<Subscriber<SavedEvent>> subscribers = new HashSet<>(); //список подписчиков

    public void setSaveEventInQueue(SavedEvent savedEvent){
        myBlockingQueue.add(savedEvent);
        notifySubscribers();
    }

    @Override
    public void subscribe(Subscriber s) {
        subscribers.add(s);
    }

    public void unsubscribe(Subscriber<SavedEvent> s){
        subscribers.remove(s);
    }

    /*public SavedEvent onNext(){
        try {
            do {
                if (!myBlockingQueue.isEmpty()) {
                    return myBlockingQueue.poll();
                }
                Thread.sleep(1000);
            } while (myBlockingQueue.isEmpty());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new SavedEvent();
    }*/

    public void notifySubscribers(){
        //если список подпищиков не пуст
        //если очередь не пуста то отправить сообщение, если очередь пуста то ждать 1 секунду и снова опросить

        while(!myBlockingQueue.isEmpty()) {
            SavedEvent currentSavedEvent = myBlockingQueue.poll();

            for (Subscriber<SavedEvent> subscriber : subscribers) {
                subscriber.onNext(currentSavedEvent);
            }

            //Thread.sleep(10000);
        }
    }

    private void sendEvent(SavedEvent currentSavedEvent){
        //разослать сообщение всем подписчикам

        for (Subscriber<SavedEvent> subscriber : subscribers) {
            subscriber.onNext(currentSavedEvent);
        }
    }

    @Override
    public void request(long n) {
        for (int i = 0; i < n; i++){

            if(!myBlockingQueue.isEmpty()){
                SavedEvent currentSavedEvent = myBlockingQueue.poll();
                sendEvent(currentSavedEvent);
            }
        }
    }

    @Override
    public void cancel() {

    }
}
