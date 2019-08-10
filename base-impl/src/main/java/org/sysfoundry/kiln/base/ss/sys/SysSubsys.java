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

package org.sysfoundry.kiln.base.ss.sys;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.sysfoundry.kiln.base.Constants;
import org.sysfoundry.kiln.base.cfg.CompositeConfigurationSource;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.cfg.PropertiesConfigurationSource;
import org.sysfoundry.kiln.base.evt.EventBus;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.ss.evt.EventSubsys;
import org.sysfoundry.kiln.base.ss.srv.ServerSubsys;
import org.sysfoundry.kiln.base.sys.*;

import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AboutSubsys(
        provisions = {
                @Key(type=ConfigurationSource.class,annotation=RuntimeSysConfigSource.class,scope=Singleton.class),
                @Key(type=ConfigurationSource.class,annotation=SysConfigSource.class,scope=Singleton.class),
                @Key(type=Set.class,valueType=ConfigurationSource.class,annotation=SysConfigSourceSet.class,scope=Singleton.class),
                @Key(type=Set.class,valueType=ConfigurationSource.class,annotation=SubsysConfigSourceSet.class,scope=Singleton.class),
                @Key(type=Sys.class,scope=Singleton.class),
                @Key(type=String[].class,annotation=Args.class,scope=Singleton.class),
                @Key(type= Validator.class,scope=Singleton.class),
                @Key(type= ValidatorFactory.class,scope=Singleton.class)
        },
        requirements = {
                @Key(type= EventBus.class)
        },
        authors = {Constants.Authors.KILN_TEAM},
        doc = "The core subsystem which constructs and instantiates the Sys being composed by the developer.",
        emits = {Sys.INITIALIZING_EVENT,
                Sys.STARTING_EVENT,
                Sys.VALIDATING_STATUS_EVENT,
                Sys.STARTED_EVENT,
                Sys.STOPPING_EVENT,
                Sys.STOPPED_EVENT},
        reactsTo = {Sys.START_FAILED_EVENT},
        provider = "url:http://www.sysfoundry.org"

)
public class SysSubsys extends Subsys {

    //public static final String NAME = SysSubsys.class.getName();

    private Optional<List<ConfigurationSource>> externalConfigurationSourcesOptional = Optional.empty();
    private String[] args;
    private Optional<List<Class>> singletonClassListOptional = Optional.empty();
    private Optional<List<Class<? extends Server>>> serverClassListOptional = Optional.empty();

    public SysSubsys(SubsysInfo subsysInfo){
        super(subsysInfo);
    }

    public SysSubsys(SubsysInfo subsysInfo,ConfigurationSource... configurationSources){
        super(subsysInfo);
        List<ConfigurationSource> configurationSourceList = Arrays.asList(configurationSources);
        externalConfigurationSourcesOptional = Optional.ofNullable(configurationSourceList);
    }

    public SysSubsys(String[] args, SubsysInfo subsysInfo,
                     List<ConfigurationSource> configurationSources, List<Class> singletonClassList,
                     List<Class<? extends Server>> serverClassList){
        super(subsysInfo);
        externalConfigurationSourcesOptional = Optional.ofNullable(configurationSources);
        this.args = args;
        this.singletonClassListOptional = Optional.ofNullable(singletonClassList);
        this.serverClassListOptional = Optional.ofNullable(serverClassList);

    }

    @Override
    protected void configure() {

        //call super configure so that moduleinfo binding happens
        super.configure();

        registerArgs();

        registerDefaultSys();

        registerSysConfigSourceSet();

        registerServerClasses();

        install(new EventSubsys());
        install(new ServerSubsys());

        registerSingletonClasses();


    }

    private void registerServerClasses() {
        serverClassListOptional.ifPresent(serverClassList->{
            bindServers(serverClassList.toArray(new Class[0]));
        });
    }

    private void registerSingletonClasses() {
        singletonClassListOptional.ifPresent(singletonClassList->{
            singletonClassList.forEach(clz->{
                bind(clz).asEagerSingleton();
            });
        });
    }

    private void registerArgs() {
        bind(new TypeLiteral<String[]>(){}).annotatedWith(Args.class).toInstance(args);
    }

    private void registerSysConfigSourceSet() {
        Multibinder<ConfigurationSource> sysConfigSourceSetMultibinder = Multibinder.newSetBinder(binder(),
                ConfigurationSource.class, SysConfigSourceSet.class);

        externalConfigurationSourcesOptional.ifPresent(configs->{
            for (ConfigurationSource config : configs) {
                if(config != null) {
                    sysConfigSourceSetMultibinder.addBinding().toInstance(config);
                }
            }

        });

        Multibinder<ConfigurationSource> subsysConfigSourceSetMultibinder = Multibinder.newSetBinder(binder(),
                ConfigurationSource.class, SubsysConfigSourceSet.class);

    }

    private void registerDefaultSys() {
        bind(Sys.class).to(DefaultSys.class).in(Singleton.class);
    }


    @Provides
    @Singleton
    @RuntimeSysConfigSource
    public ConfigurationSource provideRuntimeSysConfigurationSource(){
        PropertiesConfigurationSource systemPropertiesConfigSource = new PropertiesConfigurationSource(System.getProperties());
        return systemPropertiesConfigSource;
    }

    @Provides
    @Singleton
    @SysConfigSource
    public ConfigurationSource provideSysConfigurationSource(@RuntimeSysConfigSource ConfigurationSource runtimeSysConfigurationSource,
            @SubsysConfigSourceSet Set<ConfigurationSource> subsysConfigSourceSet,
            @SysConfigSourceSet Set<ConfigurationSource> sysConfigSourceSet){
        return new CompositeConfigurationSource(runtimeSysConfigurationSource,
                subsysConfigSourceSet,sysConfigSourceSet);
    }

    @Provides
    @Singleton
    public Validator provideValidator(ValidatorFactory validatorFactory){
        return validatorFactory.getValidator();
    }
    
    @Provides
    @Singleton
    public ValidatorFactory provideValidatorFactory(){
        return Validation.buildDefaultValidatorFactory();
    }
}
