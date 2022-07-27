package com.example.demo.genetor.reactor;

import com.example.demo.entity.SavedEvent;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class MyPublisherImpl implements Publisher<SavedEvent> {

    /*
    1. Первоначальная идея такова, создатьметод возвращающий итератор
    Итерировать нужно очередь пока есть события в ней
    Вопрос что делать когда события добавляются в очередь когда идет передача событий а обход очереди окончен?
    Как вариант пока есть хоть один подписчик в очередь будут добавлятся события
    Очереди будет две, в первую добавляются события извне и сразу передаются во вторую очередь
    Вторая очередь будет иметь более большой объем чем первая, допустим 1000 по ней будет идти итератор
    И вся эта конструкция будет находится в Flux
    смысл в том что пока есть подписчики итератор не завершает свою работу а ждет событий когда их нет
    2. добавляем удобный метод отписки
    Скорей всего придется использовать Map и отдавать пользователь id при регистрации
    */

    private final ArrayBlockingQueue<SavedEvent> queueStageOne = new ArrayBlockingQueue<>(100, true); //очередь событий
    private final ArrayBlockingQueue<SavedEvent> queueStageTwo = new ArrayBlockingQueue<>(1000, true); //очередь событий
    private final Set<Subscriber<SavedEvent>> mySubscribers = new HashSet<>();

    //private Iterator

    public void addEvent(SavedEvent savedEvent){
        queueStageOne.add(savedEvent);
    }

    @Override
    public void subscribe(Subscriber s) {
        mySubscribers.add(s);
    }

    public void unsubscribe(Subscriber<SavedEvent> s){
        mySubscribers.remove(s);
    }
}
