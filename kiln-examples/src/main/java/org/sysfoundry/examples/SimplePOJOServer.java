package org.sysfoundry.examples;

import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.OnEvent;
import org.sysfoundry.kiln.base.srv.AbstractServer;
import org.sysfoundry.kiln.base.sys.Sys;

import javax.inject.Inject;


@Slf4j
public class SimplePOJOServer extends AbstractServer {

    private ServerConfig config;

    @Inject
    public SimplePOJOServer(ServerConfig config){
        super("simple-server","simple","");
        this.config = config;
    }

    public void start(String[] args){
        log.info("Starting..."+args.length);
    }


    public void stop(){
        log.info("Stopping...");
    }

    @OnEvent({Sys.INITIALIZING_EVENT,Sys.STARTED_EVENT})
    public void onNewEvent(Event event){
        log.info("Received Event {}",event.getName());

    }
}
