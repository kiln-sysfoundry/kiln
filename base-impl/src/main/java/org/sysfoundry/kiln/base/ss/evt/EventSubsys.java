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


import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeListener;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.sys.*;
import org.sysfoundry.kiln.base.evt.EventBus;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.sysfoundry.kiln.base.Constants.Authors.KILN_TEAM;
import static org.sysfoundry.kiln.base.Constants.KILN_PROVIDER_URL;
import static org.sysfoundry.kiln.base.ss.evt.EventSubsys.ASYNCBUS_EXECUTOR;
import static org.sysfoundry.kiln.base.ss.evt.EventSubsys.CONFIG_PREFIX;
import static org.sysfoundry.kiln.base.sys.Sys.STOPPED_EVENT;
import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

@Slf4j
@About(
        doc="The Event bus subsystem provides the Eventing capability to Kiln",
        configPrefix = CONFIG_PREFIX,
        configType = EventbusConfig.class,
        provider = KILN_PROVIDER_URL,
        authors = KILN_TEAM,
        provisions = {
                @Key(type=ExecutorService.class,name=ASYNCBUS_EXECUTOR,scope=Singleton.class),
                @Key(type=EventBus.class,scope=Singleton.class)
        },
        reactsTo = STOPPED_EVENT
)
public class EventSubsys extends Subsys {

    private EventTargetListener eventTargetListener = new EventTargetListener();

    public static final String CONFIG_PREFIX = "/eventbus-config";
    public static final String ASYNCBUS_EXECUTOR = "asyncbus-executor";

    @Override
    protected void configure() {
        super.configure();

        requestInjection(eventTargetListener.getEventTypeProvisionListener());

        bindListener(Matchers.any(),eventTargetListener.getEventTypeListener());
        bindListener(Matchers.any(),eventTargetListener.getEventTypeProvisionListener());
    }

    @Provides
    @Singleton
    public EventBus provideEventBus(@Named("asyncbus-executor") ExecutorService executorService){
        return new EventBusImpl(executorService);
    }


    @Provides
    @Named(ASYNCBUS_EXECUTOR)
    @Singleton
    public ExecutorService provideExecutorService(EventbusConfig config){
        log.trace("Eventbus Config {} ",config);
        return Executors.newFixedThreadPool(config.getAsyncExecutorThreads()); //for now only one thread is assigned
    }


}
