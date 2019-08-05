package org.sysfoundry.examples;

import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.cfg.InputStreamConfigurationSource;
import org.sysfoundry.kiln.base.ss.sys.BaseSysBuilder;
import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Sys;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

public class MainWithSysBuilder {

    public static void main(String[] args) {
        Sys sys = new BaseSysBuilder(args)
                .withAttributes(new SubsysInfo("some-system",MAP(KV("name","some-system"))))
                .withSubsystems(new ServerTestSubsys())
                .withConfigurations(new InputStreamConfigurationSource(false,
                        false,Main.class.getResourceAsStream("/TestConfig.json")))
                .build();

        try {
            sys.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
