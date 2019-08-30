/*
 * Copyright 2019 Sysfoundry (www.sysfoundry.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sysfoundry.examples;

import com.typesafe.config.ConfigFactory;
import org.sysfoundry.kiln.base.LifecycleException;
import org.sysfoundry.kiln.base.cfg.InputStreamConfigurationSource;
import org.sysfoundry.kiln.base.cfg.TypesafeConfigurationSource;
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
                .withConfigurations(new TypesafeConfigurationSource(ConfigFactory.defaultApplication()))
                .build();

        try {
            sys.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
