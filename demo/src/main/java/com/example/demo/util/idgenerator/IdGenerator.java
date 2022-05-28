package com.example.demo.util.idgenerator;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdGenerator {

    public long getRandomId(){ //генерация рандомных чисел для id
        return UUID.randomUUID().getMostSignificantBits();
    } //ожидаем уникальное значение long
}
