package com.example.demoReactiveWebFlux.service.beverages;

import com.example.demoReactiveWebFlux.service.AbstractCoffeeBeverages;
import com.example.demoReactiveWebFlux.service.Beverages;

public class Espresso extends AbstractCoffeeBeverages implements Beverages {

    public Espresso(){
        final int waterConsumption = getWaterConsumption();
        final int coffeeConsumption = getCoffeeConsumption();
    }



    private final int waterConsumption = 100; // 100 мл воды
    private final int coffeeConsumption = 10; // 10 гр кофе

    @Override
    public int getWaterConsumption(){
        return waterConsumption;
    }

    @Override
    public int getCoffeeConsumption(){
        return coffeeConsumption;
    }
}