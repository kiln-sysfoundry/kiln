package org.sysfoundry.examples;

import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.srv.AbstractServer;

import javax.inject.Inject;


@Slf4j
public class SimplePOJOServer extends AbstractServer {

    private ServerConfig config;

    @Inject
    public SimplePOJOServer(ServerConfig config){
        super("simple-server","simple","");
        this.config = config;
    }

    public void start(){
        log.info("Starting...");
    }


    public void stop(){
        log.info("Stopping...");
    }
}
