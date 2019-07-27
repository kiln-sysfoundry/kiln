package org.sysfoundry.examples;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
public class YetAnotherService {

    @PostConstruct
    public void onStart(String someArg){
        log.debug("OnStart...");
    }

    @PostConstruct
    public void onStartNoArg(){
        log.debug("OnStartNoArg...");
    }

    @PreDestroy
    public void onStop(){
        log.debug("OnStop...");
    }

}
