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
        doc="Init Server for doing the initialization services",
        provides="init-service"
)
public class InitServer extends AbstractServer {

    private SimpleSubsysConfig cfg;

    @Inject
    public InitServer(SimpleSubsysConfig config){
        this.cfg = config;
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        log.info("Started with config {} ...",cfg);
    }

    @Override
    public void stop() throws LifecycleException {
        log.info("Stopped...");
    }
}
