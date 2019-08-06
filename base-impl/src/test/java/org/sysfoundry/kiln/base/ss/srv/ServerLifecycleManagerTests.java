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

import com.github.krukow.clj_ds.PersistentList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.srv.AbstractServer;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.ss.evt.EventSubsys;
import org.sysfoundry.kiln.base.sys.Sys;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ServerLifecycleManagerTests {

    @Test
    @DisplayName("test-server-lifecycle-mgr-behaviour")
    public void testServerLifecycleMgrBehaviour(){
        String[] args = new String[]{"arg1","arg2"};
        StartupInfoCollector collector = new StartupInfoCollector();
        Set<Server> servers = getTestServers(collector);
        EventBus eventBus = new EventSubsys().provideEventBus(Executors.newSingleThreadExecutor());
        ServerLifecycleManager serverLifecycleManager = new ServerLifecycleManager(eventBus,servers,args);

        eventBus.publishSync(Event.create(Sys.INITIALIZING_EVENT));

        eventBus.publishSync(Event.create(Sys.STARTING_EVENT));

        List<Server> startupSequenceActual = collector.getStartupSequence();

        List<String> expectedSequence = Arrays.asList(new String[]{"server1","server3","server4","server2"});

        List<String> actualSequence = startupSequenceActual.stream().map(Server::getName).collect(Collectors.toList());

        Assertions.assertEquals(expectedSequence,actualSequence,"Expected sequence of startup does not match actual");


    }

    private Set<Server> getTestServers(StartupInfoCollector collector) {
        HashSet<Server> serverSet = new HashSet<>();

        serverSet.add(new Server1(collector));
        serverSet.add(new Server2(collector));
        serverSet.add(new Server3(collector));
        serverSet.add(new Server4(collector));

        return serverSet;
    }
}

class StartupInfoCollector{

    private List<Server> serverStartupSequence = new ArrayList<>();

    void addInfo(Server server){
        //System.out.println("Started server "+server.getName());
        serverStartupSequence.add(server);
    }

    List<Server> getStartupSequence(){
        return serverStartupSequence;
    }
}

class Server1 extends AbstractServer {


    private StartupInfoCollector collector = null;

    Server1(StartupInfoCollector collector){
        super("server1","datasource","");
        this.collector = collector;
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        collector.addInfo(this);
    }

    @Override
    public void stop() throws LifecycleException {

    }
}

class Server2 extends AbstractServer {


    private StartupInfoCollector collector = null;

    Server2(StartupInfoCollector collector){
        super("server2","http","*");
        this.collector = collector;
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        collector.addInfo(this);
    }

    @Override
    public void stop() throws LifecycleException {

    }
}

class Server3 extends AbstractServer {


    private StartupInfoCollector collector = null;

    Server3(StartupInfoCollector collector){
        super("server3","sftp","datasource");
        this.collector = collector;
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        collector.addInfo(this);
    }

    @Override
    public void stop() throws LifecycleException {

    }
}

class Server4 extends AbstractServer {


    private StartupInfoCollector collector = null;

    Server4(StartupInfoCollector collector){
        super("server4","","sftp");
        this.collector = collector;
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        collector.addInfo(this);
    }

    @Override
    public void stop() throws LifecycleException {

    }
}
