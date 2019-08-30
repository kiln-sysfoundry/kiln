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

package org.sysfoundry.kiln.base.ss.srv;

import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.sysfoundry.kiln.base.Constants;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.health.Log;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.srv.ServerSet;
import org.sysfoundry.kiln.base.sys.AboutSubsys;
import org.sysfoundry.kiln.base.sys.Args;
import org.sysfoundry.kiln.base.sys.Key;
import org.sysfoundry.kiln.base.sys.Subsys;
import org.sysfoundry.kiln.base.util.MethodCalledMatcher;

import javax.inject.Singleton;
import java.util.Set;

import static org.sysfoundry.kiln.base.Constants.KILN_PROVIDER_URL;
import static org.sysfoundry.kiln.base.Constants.SERVER_LIFECYCLE_EVENTS;
import static org.sysfoundry.kiln.base.sys.Sys.*;


@AboutSubsys(
        doc = "The Server subsystem provides the capabilities to support the concept of Servers in Kiln",
        configType = ServerSubsysConfig.class,
        configPrefix = ServerSubsys.CONFIG_PREFIX,
        provider = KILN_PROVIDER_URL,
        authors = Constants.Authors.KILN_TEAM,
        provisions = {
                @Key(type= Set.class,annotation=ServerSet.class,scope= Singleton.class)
        },
        requirements = {
                @Key(type = String[].class,annotation = Args.class),
                @Key(type = EventBus.class)
        },
        emits = {SERVER_LIFECYCLE_EVENTS},
        reactsTo = {INITIALIZING_EVENT,STARTING_EVENT,STOPPING_EVENT}
)
public class ServerSubsys extends Subsys {

    public static final String CONFIG_PREFIX = "server-subsys-config";
    public static final String NAME = "server-subsys";

    private static final Logger log = Log.get(NAME);

    @Override
    protected void configure() {

        super.configure();

        registerServerSetMultibinder();

        bind(ServerLifecycleManager.class).asEagerSingleton();

        ServerLifecycleMethodInterceptor startMethodInterceptor = new ServerLifecycleMethodInterceptor("starting",
                "started","start-failed");
        ServerLifecycleMethodInterceptor stopMethodInterceptor = new ServerLifecycleMethodInterceptor("stopping",
                "stopped","stop-failed");

        requestInjection(startMethodInterceptor);
        requestInjection(stopMethodInterceptor);

        //bind the method interceptor for intercepting server lifecycle methods (start & stop)
        bindInterceptor(Matchers.subclassesOf(Server.class),
                MethodCalledMatcher.methodCalled("start"),
                        startMethodInterceptor);
        bindInterceptor(Matchers.subclassesOf(Server.class),
                MethodCalledMatcher.methodCalled("stop"),
                stopMethodInterceptor);




    }

    private void registerServerSetMultibinder() {
        Multibinder.newSetBinder(binder(),Server.class, ServerSet.class);
    }

}
