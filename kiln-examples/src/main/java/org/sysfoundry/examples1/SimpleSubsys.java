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

import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.sys.AboutSubsys;
import org.sysfoundry.kiln.base.sys.Key;
import org.sysfoundry.kiln.base.sys.Subsys;

import javax.inject.Singleton;

import static org.sysfoundry.kiln.base.Constants.KILN_PROVIDER_URL;

@AboutSubsys(
        doc = "This is a simple subsys to test the AboutSubsys functionality",
        configType = SimpleSubsysConfig.class,
        configPrefix = "/simple-subsys-config",
        provisions = @Key(type=SimpleService.class,scope= Singleton.class),
        requirements = @Key(type= EventBus.class),
        servers = {
                SimpleServer.class,
                InitServer.class
        },
        emits = {SimpleServer.STARTED,SimpleServer.STOPPED},
        provider = KILN_PROVIDER_URL
)
public class SimpleSubsys extends Subsys {

    @Override
    protected void configure() {
        super.configure();

        bind(SimpleService.class).in(Singleton.class);
    }
}
