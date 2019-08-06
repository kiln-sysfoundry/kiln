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

package org.sysfoundry.kiln.base.ss.sys;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.sys.Sys;

@Slf4j
class DefaultSys implements Sys{

    private EventBus eventBus;
    private boolean started;
    private boolean initialized;

    @Inject
    public DefaultSys(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Override
    public void start() throws LifecycleException {

        registerJVMShutdownHook();
        eventBus.publishSync(Event.create(INITIALIZING_EVENT));
        initialized = true;
        eventBus.publishSync(Event.create(STARTING_EVENT));
        eventBus.publishSync(Event.create(VALIDATING_STATUS_EVENT));
        started = true;
        eventBus.publishSync(Event.create(STARTED_EVENT));
    }

    private void registerJVMShutdownHook() {
        log.debug("Registering JVM Shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        },"main-shutdown-hook"));
    }

    @Override
    public boolean isStarted() {
        return started;
    }


    @Override
    public void stop() {
        if(started) {
            log.debug("Stopping Sys...");
            eventBus.publishSync(Event.create(STOPPING_EVENT));
            started = false;
            eventBus.publishSync(Event.create(STOPPED_EVENT));
        }
    }

    @Subscribe
    public void onEvent(Event event){
        if(event.getName().equalsIgnoreCase(START_FAILED_EVENT)){
            log.warn("Shutting down the system....");

            eventBus.publishSync(Event.create(STOPPING_EVENT));

            eventBus.publishSync(Event.create(STOPPED_EVENT));


            System.exit(1);
        }
    }
}
