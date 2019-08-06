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

import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.srv.ServerSet;
import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Subsys;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.sys.SysConfigSource;
import org.sysfoundry.kiln.base.util.MethodCalledMatcher;

import javax.inject.Singleton;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

public class ServerSubsys extends Subsys {

    public static final String NAME = ServerSubsys.class.getName();

    private ServerSubsysConfig defaultConfig = new ServerSubsysConfig();

    public ServerSubsys(){
        super(new SubsysInfo(NAME,MAP(KV("name",NAME))));
    }

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

    @Provides
    @Singleton
    public ServerSubsysConfig provideSubsysConfig(@SysConfigSource ConfigurationSource configurationSource){
        ServerSubsysConfig serverSubsysConfig = configurationSource.get("/server-subsys-config", ServerSubsysConfig.class, defaultConfig);
        return serverSubsysConfig;
    }

}
