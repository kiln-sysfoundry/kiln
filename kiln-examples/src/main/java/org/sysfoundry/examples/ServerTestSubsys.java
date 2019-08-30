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

import org.sysfoundry.kiln.base.sys.AboutSubsys;
import org.sysfoundry.kiln.base.sys.SubsysInfo;
import org.sysfoundry.kiln.base.sys.Subsys;

import static org.sysfoundry.kiln.base.util.CollectionUtils.KV;
import static org.sysfoundry.kiln.base.util.CollectionUtils.MAP;

@AboutSubsys
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
