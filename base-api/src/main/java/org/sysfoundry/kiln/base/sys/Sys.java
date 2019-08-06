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

import org.sysfoundry.kiln.base.LifecycleException;

/**
 * Abstraction of a System in Kiln.
 *
 */
public interface Sys {


    static final String INITIALIZING_EVENT = "sys-initializing";
    static final String STARTING_EVENT = "sys-starting";
    static final String STARTED_EVENT = "sys-started";
    static final String STOPPING_EVENT = "sys-stopping";
    static final String STOPPED_EVENT = "sys-stopped";
    static final String VALIDATING_STATUS_EVENT = "sys-validating-status";
    static final String START_FAILED_EVENT = "sys-start-failed";

    /**
     * Method invoked by  the caller to start the 'system'
     * Note that the implementation should ensure that the start() does not return till the system start is completed.
     * @throws LifecycleException Exception thrown when errors occur during system startup
     */
    void start() throws LifecycleException;

    /**
     * Retrieves whether the Sys is started or not
     * @return Flag indicating the started status of a Sys
     */
    boolean isStarted();

    /**
     * Issues a stop signal when the Sys needs to be stopped
     * Note that similar to the start() method, this method should not return till the system stop is completed.
     *
     */
    void stop();
}
