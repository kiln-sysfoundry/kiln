package org.sysfoundry.kiln.base.ss.evt;

import org.sysfoundry.kiln.base.evt.EventBus;

import java.util.concurrent.ExecutorService;

class EventBusImpl implements EventBus{


    private com.google.common.eventbus.AsyncEventBus asyncEventBus;
    private com.google.common.eventbus.EventBus eventBus;


    EventBusImpl(ExecutorService executorService){
        eventBus = new com.google.common.eventbus.EventBus("sync-eventbus");
        asyncEventBus = new com.google.common.eventbus.AsyncEventBus("async-eventbus",executorService);
    }

    @Override
    public void publishSync(Object message) {
        eventBus.post(message);
    }

    @Override
    public void publishASync(Object message) {
        asyncEventBus.post(message);
    }

    @Override
    public void register(Object subscriber) {
        eventBus.register(subscriber);
        asyncEventBus.register(subscriber);
    }

    @Override
    public void unRegister(Object subscriber) {
        eventBus.unregister(subscriber);
        asyncEventBus.unregister(subscriber);
    }
}
