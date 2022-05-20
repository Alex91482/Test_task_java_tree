package com.example.demoReactiveWebFlux.service;

public abstract class AbstractCoffeeBeverages {

    int waterConsumption;
    int coffeeConsumption;

    public int getWaterConsumption(){
        return waterConsumption;
    }

    public int getCoffeeConsumption(){
        return coffeeConsumption;
    }
}
