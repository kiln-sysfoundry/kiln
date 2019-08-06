package org.sysfoundry.examples;

import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.srv.AbstractServer;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.ss.sys.BaseSysBuilder;
import org.sysfoundry.kiln.base.sys.Sys;

public class SimpleServerExample {

    public static void main(String[] args) {
        Sys sys = new BaseSysBuilder(args)
                .withServers(SimpleServer.class)
                .build();

        try {
            sys.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j
class SimpleServer extends AbstractServer {

    SimpleServer(){
        super("simple-server");
    }

    @Override
    public void start(String[] args) throws LifecycleException {
        log.info("Starting with args {}",args.length);
        if(args.length > 0){
            log.info("First arg {}",args[0]);
        }
    }

    @Override
    public void stop() throws LifecycleException {

    }
}
