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

package org.sysfoundry.examples;

import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.OnEvent;
import org.sysfoundry.kiln.base.srv.AbstractServer;


@Slf4j
public class AnotherPOJOServer extends AbstractServer {

    public AnotherPOJOServer(){
        super("another-server","http,https","*");
    }


    public void start(String[] args){
        log.info("Starting..."+args.length);
    }


    public void stop(){
        log.info("Stopping...");
    }


    @OnEvent
    public void onEvent(Event e){
        log.debug("Received Event {}",e.getName());
    }

}
