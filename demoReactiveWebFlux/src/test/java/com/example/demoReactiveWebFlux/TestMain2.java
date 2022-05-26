package com.example.demoReactiveWebFlux;

import com.example.demoReactiveWebFlux.service.CoffeeMachineService;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    public void test() throws InterruptedException {

        AtomicInteger size1 = new AtomicInteger();
        AtomicInteger size2 = new AtomicInteger();

        myBlockingQueue.add(">> Queue: " + LocalTime.now().toString());
        Flux.fromIterable(myBlockingQueue)
                .delayElements(Duration.ofSeconds(1))
                .collectList()
                .doOnSuccess(data -> {
                    size1.set(data.size());
                    myBlockingQueue.poll();
                })
                .subscribe();


        Thread.sleep(500);
        myBlockingQueue.add(">> Queue: " + LocalTime.now().toString());
        Flux.fromIterable(myBlockingQueue)
                .delayElements(Duration.ofSeconds(1))
                .collectList()
                .doOnSuccess(data -> {
                    size2.set(data.size());
                    myBlockingQueue.poll();
                })
                .subscribe();

        Thread.sleep(3000);
        Assert.isTrue(size1.get() < size2.get(), "Size 1: " + size1.get() + " < Size 2: " + size2.get());
        Assert.isTrue(myBlockingQueue.size() == 0,  "Queue size = " + myBlockingQueue.size() + ". Queue size should be 0");
    }

    @Test
    public void testTwo() throws InterruptedException {
        //в этом тесте можно увидеть что выдерживается время извлечения элементов из очереди
        //второй подписчик получит два элемента из очереди
        //так же проверяем что после отписки в очереде не осталось элементов

        CoffeeMachineService coffeeMachineService = new CoffeeMachineService(null);

        AtomicInteger size1 = new AtomicInteger();
        AtomicInteger size2 = new AtomicInteger();

        //long m = System.currentTimeMillis();
        //System.out.println((double) (System.currentTimeMillis() - m));
        coffeeMachineService.test1()
                .collectList()
                .doOnSuccess(data -> size1.set(data.size()))
                .subscribe();

        Thread.sleep(500);

        coffeeMachineService.test1()
                .collectList()
                .doOnSuccess(data -> size2.set(data.size()))
                .subscribe();

        Thread.sleep(3000);
        Assert.isTrue(size1.get() < size2.get(), "Size 1: " + size1.get() + " < Size 2: " + size2.get());
        Assert.isTrue(coffeeMachineService.getSizeQueue() == 0,
                "Queue size = " + coffeeMachineService.getSizeQueue() + ". Queue size should be 0");
    }

}
