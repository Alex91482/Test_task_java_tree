package com.example.demo.service.beverages;


import com.example.demo.service.AbstractCoffeeBeverages;
import com.example.demo.service.Beverages;

public class Americano extends AbstractCoffeeBeverages implements Beverages {

    public Americano(){
        final int waterConsumption = getWaterConsumption();
        final int coffeeConsumption = getCoffeeConsumption();
    }

    private final int waterConsumption = 120; // 120 мл воды
    private final int coffeeConsumption = 10; // 10 гр молотого кофе

    @Override
    public int getWaterConsumption(){
        return waterConsumption;
    }

    @Override
    public int getCoffeeConsumption(){
        return coffeeConsumption;
    }
}
