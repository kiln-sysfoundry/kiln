package org.sysfoundry.kiln.base.ss.evt;

import com.google.common.eventbus.Subscribe;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.evt.Event;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Slf4j
class Subscriber {

    private Object target;
    private SubscriberMeta meta;


    Subscriber(@NonNull Object targetObject,@NonNull SubscriberMeta meta){
        this.target = targetObject;
        this.meta = meta;
    }


    @Subscribe
    public void subscribeForEvent(Event event){

        log.trace("Received event {} ",event.getName());

        Optional<List<String>> interestedEventsListOptional = meta.getInterestedEventsList();
        Optional<Method> targetMethodOptional = meta.getTargetMethod();

        log.trace("InterestedEventsListOptional {}",interestedEventsListOptional);

        interestedEventsListOptional.ifPresent(eventList->{
            log.trace("Event List {}",eventList);
            if(eventList.isEmpty() || eventList.contains(event.getName())){
                //now invoke the target method with the event information
                targetMethodOptional.ifPresent(method -> {
                    int paramCount = method.getParameterCount();
                    Object[] args = new Object[]{};
                    if(paramCount > 0){
                        //invoke with the event argument
                        args = new Object[]{event};
                    }
                    try {
                        method.invoke(target, args);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }


    public Object getTarget(){
        return this.target;
    }

}
