package org.sysfoundry.kiln.base.ss.srv;

import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import org.sysfoundry.kiln.base.srv.ServerSet;
import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Subsys;
import org.sysfoundry.kiln.base.srv.Server;
import org.sysfoundry.kiln.base.util.MethodCalledMatcher;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

public class ServerSubsys extends Subsys {

    public static final String NAME = ServerSubsys.class.getName();

    public ServerSubsys(){
        super(new SubsysInfo(NAME,MAP(KV("name",NAME))));
    }

    @Override
    protected void configure() {

        super.configure();

        registerServerSetMultibinder();

        bind(ServerLifecycleManager.class).asEagerSingleton();

        ServerLifecycleMethodInterceptor startMethodInterceptor = new ServerLifecycleMethodInterceptor("starting",
                "started","start-failed");
        ServerLifecycleMethodInterceptor stopMethodInterceptor = new ServerLifecycleMethodInterceptor("stopping",
                "stopped","stop-failed");

        requestInjection(startMethodInterceptor);
        requestInjection(stopMethodInterceptor);

        //bind the method interceptor for intercepting server lifecycle methods (start & stop)
        bindInterceptor(Matchers.subclassesOf(Server.class),
                MethodCalledMatcher.methodCalled("start"),
                        startMethodInterceptor);
        bindInterceptor(Matchers.subclassesOf(Server.class),
                MethodCalledMatcher.methodCalled("stop"),
                stopMethodInterceptor);




    }

    private void registerServerSetMultibinder() {
        Multibinder.newSetBinder(binder(),Server.class, ServerSet.class);
    }


}
