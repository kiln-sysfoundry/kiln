package org.sysfoundry.kiln.base.ss.sys;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.sysfoundry.kiln.base.cfg.CompositeConfigurationSource;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.cfg.PropertiesConfigurationSource;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.ss.evt.EventSubsys;
import org.sysfoundry.kiln.base.ss.srv.ServerSubsys;
import org.sysfoundry.kiln.base.sys.*;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @SysConfigSource
    public ConfigurationSource provideSysConfigurationSource(
            @SubsysConfigSourceSet Set<ConfigurationSource> subsysConfigSourceSet,
            @SysConfigSourceSet Set<ConfigurationSource> sysConfigSourceSet){
        PropertiesConfigurationSource systemPropertiesConfigSource = new PropertiesConfigurationSource(System.getProperties());

        return new CompositeConfigurationSource(systemPropertiesConfigSource,
                subsysConfigSourceSet,sysConfigSourceSet);
    }


}
