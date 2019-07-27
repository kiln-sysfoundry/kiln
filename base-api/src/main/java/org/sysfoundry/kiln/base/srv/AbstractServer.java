package org.sysfoundry.kiln.base.srv;

import java.util.Optional;

public abstract class AbstractServer implements Server{

    private String name;
    private Optional<String[]> providesOptional = Optional.empty();
    private Optional<String[]> requiresOptional = Optional.empty();
    private Integer startLevel = Server.UNKNOWN_LEVEL;

    public AbstractServer(String name,String providesList,String requiresList){
        this.name = name;
        if(providesList!= null && !providesList.trim().isEmpty()){
            this.providesOptional = Optional.ofNullable(providesList.split(","));
        }
        if(requiresList!= null && !requiresList.trim().isEmpty()){
            this.requiresOptional = Optional.ofNullable(requiresList.split(","));
        }

    }

    public AbstractServer(String name,String[] provides,String[] requires){
        this.name = name;
        this.providesOptional = Optional.ofNullable(provides);
        this.requiresOptional = Optional.ofNullable(requires);
    }

    public AbstractServer(String name){
        this.name = name;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getStartLevel() {
        return startLevel;
    }

    @Override
    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
    }

    @Override
    public Optional<String[]> getRequiredCapabilities() {
        return requiresOptional;
    }

    @Override
    public Optional<String[]> getProvidedCapabilities() {
        return providesOptional;
    }
}
