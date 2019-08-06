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

import org.sysfoundry.kiln.base.evt.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class EventBusImpl implements EventBus{


    private com.google.common.eventbus.AsyncEventBus asyncEventBus;
    private com.google.common.eventbus.EventBus eventBus;


    EventBusImpl(ExecutorService executorService){
        eventBus = new com.google.common.eventbus.EventBus("sync-eventbus");
        asyncEventBus = new com.google.common.eventbus.AsyncEventBus("async-eventbus",executorService);
    }

    EventBusImpl(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
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
