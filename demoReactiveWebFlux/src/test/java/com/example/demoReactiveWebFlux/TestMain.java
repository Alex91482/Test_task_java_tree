package com.example.demoReactiveWebFlux;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ArrayBlockingQueue;

public class TestMain {

    private ArrayBlockingQueue<String> myBlockingQueue = new ArrayBlockingQueue<>(100, true);
    private int counter = 0;

    private Flux<String> myFlux = Flux
            .fromIterable(myBlockingQueue)
            .delayElements(Duration.ofSeconds(1)) //имитация задержки на приготовление кофе
            ;

    private Flux<Object> myFlux1 = Flux.generate((sink -> {
        String element = myBlockingQueue.poll();
        if (element == null) {
            sink.complete();
        } else {
            sink.next(element);
        }
    })
    ).delayElements(Duration.ofSeconds(2));

    private Flux<Object> myFlux2 = Flux.create(sink -> {
        myBlockingQueue.forEach(sink::next);
        sink.complete();
    }
    ).delayElements(Duration.ofSeconds(2));


    public static void main(String...args) throws InterruptedException {
        System.out.println("Start program");

        TestMain testMain = new TestMain();
        testMain.startThreadNoStatic(6); //создаем 4 потока
        Thread.sleep(20000); //если не заблокировать основной поток то ни чего не успеет произойти

        System.out.println("End program");
    }

    private void testAddFlux3(String str){
        counter++;
        System.out.println("Start a count " + counter + " " + str);
        myBlockingQueue.add(
                ">> Queue: " + LocalTime.now().toString() + ", counter: " + counter + ", thread: " + str
        );
        /*myFlux1.subscribe(
                System.out::println,
                System.out::println,
                () -> System.out.println("To End " + str)
        );*/
        myFlux.subscribe(
                System.out::println,
                System.out::println,
                () -> myBlockingQueue.poll()
        );
        //вместо сообщения о завершении должно извлекатся событие из очереди
    }

    private class MyThread extends Thread{

        @Override
        public void run(){
            testAddFlux3(getName());
        }
    }

    private void startThreadNoStatic(int x) throws InterruptedException {
        //запускаем x потоков
        for(int i = 0; i < x; i++){
            MyThread myThread = new MyThread();
            myThread.start();
            if (i == 1 | i == 3)
                //после создания второго потока приостанавливаем
                Thread.sleep(3000);
        }
    }

    private void testAddFlux4(String str){
        counter++;
        System.out.println("Start a count " + counter + " " + str);
        myBlockingQueue.add(
                ">> Queue: " + LocalTime.now().toString() + ", counter: " + counter + ", thread: " + str
        );
        Flux.generate((sink -> {
                    String element = myBlockingQueue.poll();
                    if (element == null) {
                        sink.complete();
                    } else {
                        sink.next(element);
                    }
                })
        ).delayElements(Duration.ofSeconds(1))
                .subscribe(
                    System.out::println,
                    System.out::println,
                    () -> System.out.println("To End " + str)
        );
    }


    private void testFlux1(){

        Flux<Object> fs = Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String> builder()
                        .id(String.valueOf(sequence)) //можно использовать как идентефикакот готового кофе и вообще впаривать сюда айдишник события
                        .event("periodic-event")
                        .data("SSE - " + LocalTime.now().toString())
                        .build());

        fs.subscribe(System.out::println);
    }

    private void testFlux2(){
        //просто проверка как это работает
        Flux<Object> fx = Flux.just(1, 2, 3, 4, 5);

        fx.subscribe(System.out::println);
    }

    private void testFlux4(){
        //подписка только на 4 элемента
        //но метод устаревший
        Flux.range(1, 100)
                .subscribe(
                        System.out::println,
                        System.out::println,
                        () ->System.out.println("complete"),
                        subscription -> {
                            subscription.request(4);
                            subscription.cancel();
                        }
                );
    }

    private void testFlux5(){
        //Flux.
    }
}
