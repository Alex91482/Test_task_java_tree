package com.example.demoReactiveWebFlux;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ArrayBlockingQueue;

public class TestMain2 {

    private ArrayBlockingQueue<String> myBlockingQueue = new ArrayBlockingQueue<>(100, true);
    private int counter = 0;

    private Flux<String> myFlux = Flux
            .fromIterable(myBlockingQueue)
            .delayElements(Duration.ofSeconds(1));


    public static void main(String...args) throws InterruptedException {
        System.out.println("Start program");

        TestMain2 testMain = new TestMain2();
        testMain.startThreadNoStatic(6); //создаем 6 потоков
        Thread.sleep(20000); //если не заблокировать основной поток то ни чего не успеет произойти

        System.out.println("End program");
    }

    private void testAddFlux3(String str){
        counter++;
        System.out.println("Start a count " + counter + " " + str);
        myBlockingQueue.add(
                ">> Queue: " + LocalTime.now().toString() + ", counter: " + counter + ", thread: " + str
        );
        myFlux.subscribe(
                System.out::println,            // успех
                System.out::println,            // ошибка
                () -> myBlockingQueue.poll()    // действие при завершении потока
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
            TestMain2.MyThread myThread = new TestMain2.MyThread();
            myThread.start();
            if (i == 1 | i == 3)
                //после создания второго и четвертого потока приостанавливаем
                Thread.sleep(3000);
        }
    }

}
