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
import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Subsys;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.sys.SysConfigSource;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

@Slf4j
public class EventSubsys extends Subsys {

    public static final String NAME = EventSubsys.class.getName();

    private EventbusConfig defaultEventbusConfig = new EventbusConfig();
    private EventTargetListener eventTargetListener = new EventTargetListener();

    public EventSubsys(){
        super(new SubsysInfo(NAME,MAP(KV("name",NAME))));
        defaultEventbusConfig.setAsyncExecutorThreads(1);
    }

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
    @Named("asyncbus-executor")
    @Singleton
    public ExecutorService provideExecutorService(EventbusConfig config){
        log.trace("Eventbus Config {} ",config);
        return Executors.newFixedThreadPool(config.getAsyncExecutorThreads()); //for now only one thread is assigned
    }

    @Provides
    @Singleton
    public EventbusConfig provideEventbusConfig(@SysConfigSource ConfigurationSource configurationSource){
        EventbusConfig eventbusConfig = configurationSource.get("/eventbus-config", EventbusConfig.class,
                defaultEventbusConfig);
        return eventbusConfig;
    }

}
