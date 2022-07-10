package com.example.demo.genetor.reactor;

import com.example.demo.entity.SavedEvent;
import reactor.core.publisher.SynchronousSink;
import reactor.util.context.Context;

public class MySyncSinkImpl implements SynchronousSink<SavedEvent> {
    @Override
    public void complete() {

    }

    @Override
    public Context currentContext() {
        return null;
    }

    @Override
    public void error(Throwable throwable) {

    }

    @Override
    public void next(SavedEvent savedEvent) {

    }
}
