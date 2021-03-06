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

import com.github.krukow.clj_ds.PersistentList;
import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.health.Log;
import org.sysfoundry.kiln.base.sys.Sys;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.sysfoundry.kiln.base.ss.evt.EventSubsys.NAME;


class EventTargetListener{

    private EventTypeListener eventTypeListener = new EventTypeListener();
    private EventTypeProvisionListener eventTypeProvisionListener = new EventTypeProvisionListener();
    private PersistentList<Class> alreadyRegisteredTypes = com.github.krukow.clj_lang.PersistentList.emptyList();

    private static final Logger log = Log.get(NAME);

    EventTypeProvisionListener getEventTypeProvisionListener(){
        return eventTypeProvisionListener;
    }

    EventTypeListener getEventTypeListener(){
        return eventTypeListener;
    }

    class EventTypeProvisionListener implements ProvisionListener{

        private EventBus eventBus;
        private PersistentList<Subscriber> subscribers = com.github.krukow.clj_lang.PersistentList.emptyList();

        @Inject
        public void setEventBus(EventBus eventBus){
            this.eventBus = eventBus;

            //register the target listener as well to trap stopped events
            //and to unregister the subscribers if any
            this.eventBus.register(this);
        }

        @Override
        public <T> void onProvision(ProvisionInvocation<T> provisionInvocation) {
            Binding<T> binding = provisionInvocation.getBinding();
            Object potentialTargetInstance = provisionInvocation.provision();
            Class actualClz = potentialTargetInstance.getClass();
            Optional<SubscriberMeta> subscriberMetaOptional = eventTypeListener.getSubscriberMeta(actualClz);
            subscriberMetaOptional.ifPresent(subscriberMeta -> {

                if(subscriberMeta.hasMultipleOnEventMethods()){
                    log.info("Class {} has multiple methods annotated with OnEvent. Only one method will be chosen",subscriberMeta.getSubscriberType());
                }

                Subscriber subscriber = new Subscriber(potentialTargetInstance,subscriberMeta);
                eventBus.register(subscriber);
                subscribers = subscribers.plus(subscriber);
            });

        }

        @Subscribe
        void onEvent(Event event){
            if(event.getName().equalsIgnoreCase(Sys.STOPPED_EVENT)){
                unRegisterAllSubscribers();
            }
        }

        private void unRegisterAllSubscribers() {
            log.trace("Unregistering {} subscribers",subscribers.size());

            subscribers.forEach(subscriber -> {
                eventBus.unRegister(subscriber);
                log.trace("Un Registered Subscriber {}",subscriber.getTarget());
            });
        }


    }

    class EventTypeListener implements TypeListener{

        private PersistentMap<Class,SubscriberMeta> subscriberMetaMap = PersistentHashMap.emptyMap();

        @Override
        public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
            Class<? super I> clz = typeLiteral.getRawType();
            SubscriberMeta subscriberMeta = new SubscriberMeta(clz);
            if(subscriberMeta.isValidEventTarget()){
                subscriberMetaMap = subscriberMetaMap.plus(clz,subscriberMeta);
            }
        }

        Map<Class,SubscriberMeta> getSubscriberTypeMap(){
            return subscriberMetaMap;
        }


        Optional<SubscriberMeta> getSubscriberMeta(Class clz){
            Optional<SubscriberMeta> subscriberMetaOptional = Optional.empty();
            //loop through the list of types and check if they are assignable from the specified type
            Set<Class> keySet = subscriberMetaMap.keySet();
            for (Class aClass : keySet) {
                if(aClass.isAssignableFrom(clz)){
                    subscriberMetaOptional = Optional.ofNullable(subscriberMetaMap.get(aClass));
                    break;
                }
            }
            return subscriberMetaOptional;
        }
    }



}
