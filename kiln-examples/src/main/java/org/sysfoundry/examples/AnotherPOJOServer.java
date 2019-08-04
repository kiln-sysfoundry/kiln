package org.sysfoundry.examples;

import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.OnEvent;
import org.sysfoundry.kiln.base.srv.AbstractServer;


@Slf4j
public class AnotherPOJOServer extends AbstractServer {

    public AnotherPOJOServer(){
        super("another-server","http,https","*");
    }


    public void start(){
        log.info("Starting...");
    }


    public void stop(){
        log.info("Stopping...");
    }


    @OnEvent
    public void onEvent(Event e){
        log.debug("Received Event {}",e.getName());
    }

}
