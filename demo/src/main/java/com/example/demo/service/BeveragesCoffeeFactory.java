package com.example.demo.service;


import com.example.demo.service.beverages.Americano;
import com.example.demo.service.beverages.DoubleEspresso;
import com.example.demo.service.beverages.Espresso;

import org.springframework.stereotype.Service;

@Service
public class BeveragesCoffeeFactory {
    //что бы сделать расширение асортимента просто добавлением классов используем фабрику
    //в зависимости от того какой кофе будет запрошен такой экземпляр и создаем
    //если такого кофе в асортименте нет возвращаем null

    public AbstractCoffeeBeverages createCoffeeBeverages(String coffee){

        return switch (coffee) {
            case ("Americano") -> new Americano();
            case ("Espresso") -> new Espresso();
            case ("DoubleEspresso") -> new DoubleEspresso();
            default -> null;
        };
    }
}