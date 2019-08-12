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

import com.google.inject.Injector;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.cfg.InputStreamConfigurationSource;
import org.sysfoundry.kiln.base.srv.AboutServer;
import org.sysfoundry.kiln.base.srv.AbstractServer;
import org.sysfoundry.kiln.base.ss.sys.BaseSysBuilder;
import org.sysfoundry.kiln.base.sys.Sys;

import javax.inject.Inject;
import java.io.*;

public class SimpleMain {

    public static void main(String[] args){
        Sys sys = new BaseSysBuilder(args)
                .withConfigurations(new InputStreamConfigurationSource(false,false,
                        SimpleMain.class.getResourceAsStream("/org/sysfoundry/examples1/ext-config.json")))
                .withSubsystems(new SimpleSubsys(),new GraphvizModule())
                .withServers(OnStartServer.class)
                .build();

        try {
            sys.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j
@AboutServer()
class OnStartServer extends AbstractServer{


    private GraphvizGrapher grapher;
    private Injector injector;

    @Inject
    public OnStartServer(GraphvizGrapher graphvizGrapher, Injector injector){
        this.grapher = graphvizGrapher;
        this.injector = injector;
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        try {
            PrintWriter out = new PrintWriter(new File("SimpleViz.dot"), "UTF-8");
            grapher.setOut(out);
            grapher.setRankdir("TB");
            grapher.graph(injector);
            log.info("Done generating graph for file SimpleViz.dot");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() throws LifecycleException {

    }
}