package org.sysfoundry.examples;

import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Subsys;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

public class ServerTestSubsys extends Subsys {

    public static final String NAME = ServerTestSubsys.class.getName();

    public ServerTestSubsys(){
        super(new SubsysInfo(NAME,MAP(KV("name",NAME))));
    }
    @Override
    protected void configure() {
        super.configure();
        bindServers(
                SimplePOJOServer.class,
                AnotherPOJOServer.class
        );
    }
}
