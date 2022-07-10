package com.example.demo.service;

import com.example.demo.dao.SavedEventDAOImpl;
import com.example.demo.entity.SavedEvent;
import com.example.demo.service.beverages.AbstractCoffeeBeverages;
import com.example.demo.service.beverages.EnumBeverages;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CoffeeMachineService {

    private SavedEventDAOImpl savedEventDAOImpl; //взаимодействие с бд
    private BeveragesCoffeeFactory beveragesCoffeeFactory; //фабрика по созданию напитков
    //private IdGenerator idGenerator; //генератор id для сущностей

    public CoffeeMachineService(SavedEventDAOImpl savedEventDAOImpl, BeveragesCoffeeFactory beveragesCoffeeFactory
                /*,IdGenerator idGenerator*/){
        this.savedEventDAOImpl = savedEventDAOImpl;
        this.beveragesCoffeeFactory = beveragesCoffeeFactory;
        //this.idGenerator = idGenerator;
    }

    //размер очереди ограничен 100 записями, далее они будут перезаписыватся
    private final ArrayBlockingQueue<SavedEvent> myBlockingQueue = new ArrayBlockingQueue<>(100, true);

    private final Flux<SavedEvent> fx = Flux.fromIterable(myBlockingQueue)
            .delayElements(Duration.ofSeconds(7))
            .map(data ->{
                myBlockingQueue.poll();
                return data;
            })
            .repeat(); //когда поток заканчивается подписка остается, уверен что это жуткий говнокод но пока идей нет

    private final ConnectableFlux<SavedEvent> cf = fx.publish(); //поток теперь разадется на всех один

    public Flux<String> test1(SavedEvent savedEvent) {

        AtomicBoolean flag = new AtomicBoolean(false);
        /*Mono<String> n = new Mono<>() { //это штука должна бы работать как сигнал к отписке
            @Override
            public void subscribe(CoreSubscriber<? super String> coreSubscriber) {

            }
        };*/

        myBlockingQueue.add(savedEvent);
        cf.connect(); //запускаем на всех один поток публикации
        //добавить отписку при совпадении айди

        //возможно стоит создать новый поток из этого потока и возвращать его и отписыватся как следствие от нового потока
        /*return Flux.from(cf)
                .takeUntilOther(n)
                .doOnNext(data -> {
                    if(data.getId() == savedEvent.getId()){
                        //data.setOccurredEvent("Your order has been completed");
                        //как отписатся то???
                        System.out.println("Event " + data.getOccurredEvent());
                        //n = Mono.just("f");
                        stopVoid(n);
                    }
                })
                .map(SavedEvent::getOccurredEvent);*/

        return cf
                .takeUntilOther(st(flag.get()))
                .doOnNext(data -> {
                    if(data.getId() == savedEvent.getId()){
                        //data.setOccurredEvent("Your order has been completed");
                        //как отписатся то???
                        System.out.println("flag = " + flag.get());
                        System.out.println("Event " + data.getOccurredEvent());
                        flag.set(true);
                        System.out.println("flag = " + flag.get());
                    }
                })
                .map(SavedEvent::getOccurredEvent);
    }

    private Mono<String> st(boolean flag){
        System.out.println("flag in st = " + flag);
        //есть один подвох например что если остановка происходит только при получении следующего элемента
        //если было заказанно только одно кофе то все подвиснет пока не будет заказанно следущее кофе
        if(flag) {
            System.out.println("Mono just");
            return Mono.just("");
        }else{
            return new Mono<>() { //это штука должна бы работать как сигнал к отписке
                @Override
                public void subscribe(CoreSubscriber<? super String> coreSubscriber) {

                }
            }; //тут событие не должно пораждвтся
        }
    }

   /* private void s(ConnectableFlux<SavedEvent> c){
        //c. ;
    }
    private void stopVoid(Mono<String> mono){
        System.out.println(">> Mono just");
        mono = Mono.just("f");
        //mono.just("");
    }

    private Mono<String> stop(Mono<String> n){
        //n = Mono.just("f");
        //cf.distinct() ;
        //return Mono.just("f");
        return n.just("");
    }*/

    public int getSizeQueue(){
        return myBlockingQueue.size();
    }

    public boolean areThereEnoughCoffeeIngredients(EnumBeverages typeBeverages){
        //достаточно ли ингредиентов для кофе
        //делаем запрос на последнюю сущьность и вычитаем потребляемую воду и потребляемое кофе
        //если хоть одно число стало меньше нуля то возвращаем фолз

        return getLatestRecord().subscribe(result -> {
                    stage1(result, typeBeverages);
                }).isDisposed();
    }

    private boolean stage1(SavedEvent lastSavedEvent, EnumBeverages typeBeverages){
        //проводим вычитания из текущих показаний хватит ли на напиток кофе и воды
        AbstractCoffeeBeverages beverages = beveragesCoffeeFactory.createCoffeeBeverages(typeBeverages);
        int waterLevel = lastSavedEvent.getFillTheWaterTank() - beverages.getWaterConsumption();
        int coffeeLevel = lastSavedEvent.getFillCoffeeTank() - beverages.getCoffeeConsumption();

        return waterLevel >= 0 && coffeeLevel >= 0;
    }

    public Mono<SavedEvent> getLatestRecord(){
        //получить последнее событие
        return savedEventDAOImpl.getTheLatestEntry();
    }

    public Mono<List<SavedEvent>> getByOccurredEvent(){
        //получить все события "Coffee Machine start" используется псрото для примера
        return savedEventDAOImpl.findByOccurredName("Coffee Machine start").collectList();
    }
}
