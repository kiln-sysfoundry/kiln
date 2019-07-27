package org.sysfoundry.examples;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.cfg.InputStreamConfigurationSource;
import org.sysfoundry.kiln.base.ss.sys.SysSubsys;
import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Sys;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

public class Main {

    public static void main(String[] args) {

        InputStreamConfigurationSource configurationSource = new InputStreamConfigurationSource(false,
                false,Main.class.getResourceAsStream("/TestConfig.json"));

        Injector injector = Guice.createInjector(
                new ServerTestSubsys(),
                new SysSubsys(new SubsysInfo("kiln-examples-sys",MAP(KV("name","kiln-examples-sys")))
                        //,configurationSource
                ));

        Sys sys = injector.getInstance(Sys.class);

        try {
            sys.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }


    }
}
