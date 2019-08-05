package org.sysfoundry.examples;

import org.sysfoundry.kiln.base.evt.OnEvent;
import org.sysfoundry.kiln.base.sys.Args;
import org.sysfoundry.kiln.base.sys.Sys;

import javax.inject.Inject;

public class OnStartupRunner{

    private String[] commandArgs;

    @Inject
    OnStartupRunner(@Args String[] args){
        this.commandArgs = args;
    }

    @OnEvent(Sys.STARTED_EVENT)
    public void onStarted(){
        System.out.println("Starting up system with arguments "+commandArgs);
    }
}
