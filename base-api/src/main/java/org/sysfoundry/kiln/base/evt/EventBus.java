package org.sysfoundry.kiln.base.evt;

public interface EventBus {

    public void publishSync(Object message);

    public void publishASync(Object message);


    public void register(Object subscriber);

    public void unRegister(Object subscriber);


}
