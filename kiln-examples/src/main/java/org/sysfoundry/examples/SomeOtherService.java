package org.sysfoundry.examples;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

@Slf4j
public class SomeOtherService {

    private YetAnotherService anotherService;

    @Inject
    public SomeOtherService(YetAnotherService s){
        anotherService = s;
    }

    @PostConstruct
    public void onStart(){
        log.debug("OnStart..."+anotherService);
    }

    @PreDestroy
    public void onStop(){
        log.debug("OnStop...");
    }
}
