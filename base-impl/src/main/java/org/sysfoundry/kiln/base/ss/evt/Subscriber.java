/*
 * Copyright 2019 Sysfoundry (www.sysfoundry.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sysfoundry.kiln.base.ss.evt;

import com.google.common.eventbus.Subscribe;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.health.Log;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.sysfoundry.kiln.base.ss.evt.EventSubsys.NAME;


class Subscriber {

    private Object target;
    private SubscriberMeta meta;

    private static final Logger log = Log.get(NAME);

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
