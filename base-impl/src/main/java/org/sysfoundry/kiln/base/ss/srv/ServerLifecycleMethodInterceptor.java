package org.sysfoundry.kiln.base.ss.srv;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.evt.Event;
import org.sysfoundry.kiln.base.evt.EventBus;

import javax.inject.Inject;

@Slf4j
class ServerLifecycleMethodInterceptor implements MethodInterceptor {

    private EventBus eventBus;
    private static final String eventNameFormat = "%s-%s";
    private String beginName;
    private String endName;
    private String failName;

    ServerLifecycleMethodInterceptor(String beginName,String endName,String failName){
        this.beginName = beginName;
        this.endName = endName;
        this.failName = failName;
    }

    @Inject
    public void setEventBus(EventBus eventBus){
        this.eventBus = eventBus;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        log.trace("Invoking {}",methodInvocation.getMethod());
        Object targetInstance = methodInvocation.getThis();
        Server server = null;
        String name = null;
        if(targetInstance instanceof Server){
            server = (Server)targetInstance;
            name = server.getName();
            String eventName = String.format(eventNameFormat,name,beginName);
            eventBus.publishASync(Event.create(eventName));
        }
        Object retVal = null;
        try {
            retVal = methodInvocation.proceed();

            if(server != null){
                String eventName = String.format(eventNameFormat,name,endName);
                eventBus.publishASync(Event.create(eventName));
            }
            return retVal;

        }catch(Exception e){
            if(server != null){
                String eventName = String.format(eventNameFormat,name,failName);
                eventBus.publishASync(Event.create(eventName));
            }

            //handle exception
            throw e;
        }

    }



}
