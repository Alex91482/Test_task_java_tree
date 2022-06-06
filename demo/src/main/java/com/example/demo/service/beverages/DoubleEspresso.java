package com.example.demo.service.beverages;


public class DoubleEspresso extends AbstractCoffeeBeverages implements Beverages {

    public DoubleEspresso(){
        final int waterConsumption = getWaterConsumption(); // 100 мл воды
        final int coffeeConsumption = getCoffeeConsumption(); // 20 мл кофе
    }

    private final int waterConsumption = 100; // 100 мл воды
    private final int coffeeConsumption = 20; // 20 мл кофе

    @Override
    public int getWaterConsumption(){
        return waterConsumption;
    }

    @Override
    public int getCoffeeConsumption(){
        return coffeeConsumption;
    }
}