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

package org.sysfoundry.kiln.base.sys;

/**
 * The SysBuilder is an abstraction of a System builder in kiln.
 * SysBuilder instances can compose and build a system as specified by the developer of the system.
 */
public interface SysBuilder {

    /**
     * The build method is expected to consider all the specifications used during the Sys building process
     * and then return a fully constructed Sys instance. The Sys instance allows the boot strap script to start he instance of Sys
     * @return The Sys which is used to start the system
     */
    Sys build();
}
