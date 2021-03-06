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
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.srv.AboutServer;
import org.sysfoundry.kiln.base.srv.AbstractServer;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.ss.sys.BaseSysBuilder;
import org.sysfoundry.kiln.base.sys.Sys;

public class SimpleServerExample {

    public static void main(String[] args) {
        Sys sys = new BaseSysBuilder(args)
                .withServers(SimpleServer.class)
                .build();

        try {
            sys.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j
@AboutServer(
        doc="Simple Server For testing"
)
class SimpleServer extends AbstractServer {


    @Override
    public void start(String[] args) throws LifecycleException {
        log.info("Starting with args {}",args.length);
        if(args.length > 0){
            log.info("First arg {}",args[0]);
        }
    }

    @Override
    public void stop() throws LifecycleException {

    }
}
