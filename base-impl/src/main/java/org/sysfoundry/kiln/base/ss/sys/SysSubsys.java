package org.sysfoundry.kiln.base.ss.sys;

import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import org.sysfoundry.kiln.base.cfg.CompositeConfigurationSource;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.ss.evt.EventSubsys;
import org.sysfoundry.kiln.base.ss.srv.ServerSubsys;
import org.sysfoundry.kiln.base.sys.*;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

public class SysSubsys extends Subsys {

    //public static final String NAME = SysSubsys.class.getName();

    private Optional<ConfigurationSource[]> externalConfigurationSourcesOptional = Optional.empty();

    public SysSubsys(SubsysInfo subsysInfo){
        super(subsysInfo);
    }

    public SysSubsys(SubsysInfo subsysInfo,ConfigurationSource... configurationSources){
        super(subsysInfo);
        externalConfigurationSourcesOptional = Optional.ofNullable(configurationSources);
    }

    @Override
    protected void configure() {

        //call super configure so that moduleinfo binding happens
        super.configure();

        registerDefaultSys();

        registerSysConfigSourceSet();

        install(new EventSubsys());
        install(new ServerSubsys());
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
        return new CompositeConfigurationSource(subsysConfigSourceSet,sysConfigSourceSet);
    }


}
