package org.sysfoundry.kiln.base.ss.evt;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.evt.OnEvent;

public class EventBusTests {

    @DisplayName("Test Event Subscriber using OnEvent Annotation")
    @Test
    public void testSubscriber(){
        EventBus eventBus = new EventBusImpl();
        SimpleSubscriber simpleSubscriber = new SimpleSubscriber();
        Subscriber subscriber = new Subscriber(simpleSubscriber,new SubscriberMeta(SimpleSubscriber.class));
        eventBus.register(subscriber);
        AllEventSubscriber allEventSubscriber = new AllEventSubscriber();
        subscriber = new Subscriber(allEventSubscriber,new SubscriberMeta(AllEventSubscriber.class));
        eventBus.register(subscriber);
        SubscriberWithMultipleMethods subscriberWithMultipleMethods = new SubscriberWithMultipleMethods();
        subscriber = new Subscriber(subscriberWithMultipleMethods,new SubscriberMeta(SubscriberWithMultipleMethods.class));
        eventBus.register(subscriber);
        Subclass subclassMethods = new Subclass();
        subscriber = new Subscriber(subclassMethods,new SubscriberMeta(SubscriberWithMultipleMethods.class));
        eventBus.register(subscriber);

        eventBus.publishSync(Event.create("someevent"));
        eventBus.publishSync(Event.create("anotherevent"));
    }
}


@Slf4j
class SimpleSubscriber{

    @OnEvent("someevent")
    void onEventCallback(){
        System.out.println("Event someevent invoked");
    }
}

class AllEventSubscriber{

    @OnEvent
    void onAllEvents(Event event){
        System.out.println(String.format("Event %s received",event.getName()));
    }
}

class SubscriberWithMultipleMethods{

    @OnEvent
    void onEvent(){
        System.out.println(String.format("Event got something..."));
    }

    @OnEvent
    void onAnotherEventMethod(Event e){
        System.out.println(String.format("AnotherEvent %s got",e.getName()));
    }
}

class Subclass extends SubscriberWithMultipleMethods{

    void onAnotherEventMethod(Event e){
        System.out.println(String.format("Subclass %s got",e.getName()));
    }

}