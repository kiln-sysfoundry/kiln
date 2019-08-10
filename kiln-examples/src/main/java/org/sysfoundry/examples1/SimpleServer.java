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

package org.sysfoundry.examples1;

import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.srv.AboutServer;
import org.sysfoundry.kiln.base.srv.AbstractServer;

import javax.inject.Inject;

@Slf4j
@AboutServer(
        doc = "A simple server to demonstrate the capabilities of Kiln",
        provides = "simple-service",
        requires = "init-service"
)
public class SimpleServer extends AbstractServer {
    public static final String STARTED = "simple-server-started";
    public static final String STOPPED = "simple-server-stopped";

    private SimpleService service;

    @Inject
    public SimpleServer(SimpleService service){
        this.service = service;
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        log.info("Message : {}",service.getMessage());
    }

    @Override
    public void stop() throws LifecycleException {
        log.info("Stopped...");
    }
}
