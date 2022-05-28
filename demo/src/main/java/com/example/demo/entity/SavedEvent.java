package com.example.demo.entity;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedEvent {

    @Id
    @Getter @Setter
    private long id;                    //айди записи состояния

    @Getter @Setter
    private String occurredEvent;       //произошедшее событие

    @Getter @Setter
    private LocalDateTime eventTime;    //время когда произошло событие

    @Getter @Setter
    private int fillTheWaterTank;       //уровень воды в баке

    @Getter @Setter
    private int fillCoffeeTank;         //заполнение бака кофе

    //максимальная вместимость для данной кофемашины
    @Transient
    @Getter
    private static final int maxWaterLevel = 1000; //максимальный уровень воды в баке
    @Transient
    @Getter
    private static final int maxCoffeeLevel = 1000; //мксимльный уровень коффе

    @Override
    public String toString(){
        return new StringBuilder()
                .append("Saved Event = [ id = ")
                .append(id)
                .append(", occurred Event = ")
                .append(occurredEvent)
                .append(", event time = ")
                .append(eventTime)
                .append(", fill the water tank = ")
                .append(fillTheWaterTank)
                .append(", fill the coffee tank = ")
                .append(fillCoffeeTank)
                .append(" ]")
                .toString();
    }
}
