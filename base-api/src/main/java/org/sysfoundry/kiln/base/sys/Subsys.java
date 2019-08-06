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

package org.sysfoundry.kiln.base.sys;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.cfg.InputStreamConfigurationSource;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.srv.ServerSet;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;

/**
 * The Subsys represents an Abstraction of the Subsys in Kiln.
 * The Subsys provides the below capabilities for any Sub system extending from this Abstract Guice Module.
 * <ul>
 *     <li>Subsys Configuration support - Ability to load subsys configuration from the Subsys's package</li>
 *     <li>SubsysInfo support - Ability to register SubsysInfo provided by the concreted child subsystem</li>
 * </ul>
 */
@Slf4j
public abstract class Subsys extends AbstractModule {

    public static final String DEFAULT_CONFIG_NAME = "config.json";

    private SubsysInfo subsysInfo;

    /**
     * Creates a Subsys instance using the {@link SubsysInfo}
     * @param subsysInfo The subsys info for the subsys
     */
    public Subsys(SubsysInfo subsysInfo){
        this.subsysInfo = subsysInfo;
    }

    @Override
    protected void configure() {
        registerSubsysInfo();
        registerSubsysConfigSource();
    }

    protected void registerSubsysConfigSource() {
        try(InputStream resourceAsStream = getClass().getResourceAsStream(DEFAULT_CONFIG_NAME)){
            if(resourceAsStream != null){
                InputStreamConfigurationSource inputStreamConfigurationSource =
                        new InputStreamConfigurationSource(false,true,resourceAsStream);
                registerConfigSource(inputStreamConfigurationSource);
            }else{
                log.trace("Unable to find Subsys config {} for {} in classpath!",DEFAULT_CONFIG_NAME,subsysInfo.getID());
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("Failed to read {}, so ignoring config file..",DEFAULT_CONFIG_NAME);
        }

    }

    protected void registerConfigSource(ConfigurationSource configurationSource) {
        Multibinder<ConfigurationSource> configSources = Multibinder.newSetBinder(binder(),ConfigurationSource.class,
                SubsysConfigSourceSet.class);
        configSources.addBinding().toInstance(configurationSource);
        log.trace("Registered Subsys config for {}",subsysInfo.getID());

    }

    protected void registerSubsysInfo(){

        //bind the SubSystem` information
        Multibinder<SubsysInfo> subsysInfoMultibinder = Multibinder.newSetBinder(binder(), SubsysInfo.class, SubsysSet.class);
        subsysInfoMultibinder.addBinding().toInstance(subsysInfo);
        log.trace("Registering SubsysInfo - {}",subsysInfo);
    }

    protected void bindServers(Class<? extends Server>... servers){
        if(servers != null && servers.length>0) {
            Multibinder<Server> serverMultibinder = Multibinder.newSetBinder(binder(), Server.class, ServerSet.class);
            for (Class<? extends Server> server : servers) {
                serverMultibinder.addBinding().to(server).in(Singleton.class);
                log.trace("Bound Class {} to annotation {} set",server, ServerSet.class);
            }

        }
    }
}
