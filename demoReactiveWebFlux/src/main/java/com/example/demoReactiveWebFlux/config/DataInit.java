package com.example.demoReactiveWebFlux.config;

import com.example.demoReactiveWebFlux.dao.SavedEventDAOImpl;
import com.example.demoReactiveWebFlux.entity.SavedEvent;

import com.example.demoReactiveWebFlux.util.idgenerator.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInit implements ApplicationRunner {
    //при каждом новом запуске кофемашины заполняем баки с водой и кофе

    private SavedEventDAOImpl savedEventDAOImpl;
    private IdGenerator idGenerator;

    @Autowired
    public DataInit(SavedEventDAOImpl savedEventDAOImpl, IdGenerator idGenerator){
        this.savedEventDAOImpl = savedEventDAOImpl;
        this.idGenerator = idGenerator;
    }

    @Override
    public void run(ApplicationArguments args){

        /*savedEventDAOImpl.save(new SavedEvent().builder()
                .id(idGenerator.getRandomId())
                .occurredEvent("Coffee Machine start")
                .eventTime(LocalDateTime.now())
                .fillTheWaterTank(1000)
                .fillCoffeeTank(1000)
                .build()
        );*/

        System.out.println(">> Data init complete");
    }
}
