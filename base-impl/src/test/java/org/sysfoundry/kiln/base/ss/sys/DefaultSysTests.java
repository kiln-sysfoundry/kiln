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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.ss.evt.EventSubsys;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static org.sysfoundry.kiln.base.sys.Sys.*;

public class DefaultSysTests {

    @DisplayName("test-defaultsys-startup-sequence")
    @Test
    public void testDefaultSysStartupSequence() throws LifecycleException {
        EventSubsys subsys = new EventSubsys();
        EventBus eventBus = subsys.provideEventBus(Executors.newSingleThreadExecutor());

        TestListener testListener = new TestListener();

        //register a dummy listener
        eventBus.register(testListener);

        DefaultSys defaultSys = new DefaultSys(eventBus);

        defaultSys.start();

        String[] expected = new String[]{INITIALIZING_EVENT,STARTING_EVENT,VALIDATING_STATUS_EVENT,STARTED_EVENT};

        Assertions.assertArrayEquals(expected,testListener.getEventsSequence().toArray(),
                "Expected set of startup events do not match");

    }
}

@Slf4j
class TestListener{

    List<String> eventsSequence = new ArrayList<>();

    @Subscribe
    public void onEvent(Event e){
      //log.info("Received Event {}",e.getName());
      eventsSequence.add(e.getName());
    }

    List<String> getEventsSequence(){
        return eventsSequence;
    }


}