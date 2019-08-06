package org.sysfoundry.kiln.base.ss.sys;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.sysfoundry.kiln.base.cfg.ConfigurationSource;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Sys;
import org.sysfoundry.kiln.base.sys.SysBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

public class BaseSysBuilder implements SysBuilder {

    private Optional<String[]> sysArgsOptional = Optional.empty();
    private Optional<SubsysInfo> subsysInfoOptional = Optional.empty();
    private Optional<List<Module>> subsystemsOptional = Optional.empty();
    private Optional<List<ConfigurationSource>> configurationSourcesOptional = Optional.empty();
    private SubsysInfo defaultSubsysInfo = new SubsysInfo("anonymous-sys",MAP(KV("name","anonymous-sys")));
    private List<ConfigurationSource> defaultConfigurSourcesList;
    private String[] sysArgsDefault = new String[]{}; // default args
    private Optional<List<Class>> singletonClassListOptional = Optional.empty();
    private Optional<List<Class<? extends Server>>> serverClassListOptional = Optional.empty();
    private List<Class> defaultSingletonClassList = new ArrayList<>();
    private List<Class<? extends Server>> defaultServerClassList = new ArrayList<>();


    public BaseSysBuilder(){
        this(null);
    }

    public BaseSysBuilder(String[] args) {
        sysArgsOptional = Optional.ofNullable(args);
    }

    public BaseSysBuilder withAttributes(SubsysInfo subsysInfo) {
        subsysInfoOptional = Optional.ofNullable(subsysInfo);
        return this;
    }

    public BaseSysBuilder withSubsystems(Module... subsystems) {
        Optional<Module[]> tempModules = Optional.ofNullable(subsystems);

        tempModules.ifPresent(modules -> {
            List<Module> moduleList = Arrays.asList(modules);
            subsystemsOptional = Optional.ofNullable(moduleList);
        });

        return this;
    }

    public BaseSysBuilder withConfigurations(ConfigurationSource... configurationSources) {
        Optional<ConfigurationSource[]> tempConfigSourcesOptional = Optional.ofNullable(configurationSources);

        tempConfigSourcesOptional.ifPresent(configSources -> {
            List<ConfigurationSource> configurationSourceList = Arrays.asList(configSources);
            configurationSourcesOptional = Optional.ofNullable(configurationSourceList);
        });

        return this;
    }

    @Override
    public Sys build(){

        Module[] finalModuleList = getFinalModuleList();

        Injector mainInjector = Guice.createInjector(finalModuleList);

        Sys sys = mainInjector.getInstance(Sys.class);

        return sys;
    }

    private Module[] getFinalModuleList() {
        List<Module> finalModuleList = new ArrayList<>();

        //add the SysSubsys automatically
        SysSubsys sysSubsys = new SysSubsys(sysArgsOptional.orElse(sysArgsDefault),subsysInfoOptional.orElse(defaultSubsysInfo),
                configurationSourcesOptional.orElse(defaultConfigurSourcesList),
                singletonClassListOptional.orElse(defaultSingletonClassList),
                serverClassListOptional.orElse(defaultServerClassList));

        finalModuleList.add(sysSubsys);


        //add the rest of the modules if present
        subsystemsOptional.ifPresent(modules -> {
            finalModuleList.addAll(modules);
        });


        return finalModuleList.toArray(new Module[0]);
    }

    public BaseSysBuilder withSingletons(Class... singletonClasses) {
        List<Class> classList = Arrays.asList(singletonClasses);
        singletonClassListOptional = Optional.ofNullable(classList);
        return this;
    }

    public BaseSysBuilder withServers(Class<? extends Server>... servers){
        List<Class<? extends Server>> serverClassList = Arrays.asList(servers);
        serverClassListOptional = Optional.ofNullable(serverClassList);
        return this;
    }
}
