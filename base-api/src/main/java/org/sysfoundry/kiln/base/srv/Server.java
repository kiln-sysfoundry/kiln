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

package org.sysfoundry.kiln.base.srv;

import org.sysfoundry.kiln.base.LifecycleException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * The Server provides the ability for auto start and auto shutdown service capability in Kiln.
 * A service in Kiln should implement this interface and to express the below capabilities as expected in kiln.
 * <ol>
 *     <li>Name - Name of the server. This should be unique within the system composed by the developer</li>
 *     <li>RequiredCapabilities - The set of capabilities expected by the server before it can start</li>
 *     <li>ProvidedCapabilities - The set of capabilities provided by the server to its environment after it starts successfully</li>
 *     <li>StartLevel - The start Level attribute is used by Kiln to designate and start the server at the appropriate level</li>
 * </ol>
 */
public interface Server {

    public static final int ORPHAN_LEVEL = -2;
    public static final int UNKNOWN_LEVEL = -1;

    /**
     * Retrieves the name of the server. The Developer needs to ensure that the name of the server is unique within the context of the system
     *
     * @return - The name of the server
     */
    public String getName();

    /**
     * Retrieves the startlevel designated by Kiln
     * @return - The Start level of the server
     */
    public Integer getStartLevel();

    /**
     * The start level designated by Kiln. The developer should not tamper with this start level.
     * @param startLevel - The start level designated by Kiln
     */
    public void setStartLevel(Integer startLevel);

    /**
     * Retrieves the Optional list of Required capabilities.
     * @return - The Required capabilities which are optional
     */
    public default Optional<String[]> getRequiredCapabilities(){
        return Optional.of(new String[]{});
    }

    /**
     * Retrieves the Optional list of Provided capabilities.
     * @return - The Provided capabilities which are optional
     */
    public default Optional<String[]> getProvidedCapabilities(){
        return Optional.of(new String[]{});
    }

    /**
     * The callback invoked by Kiln when it is appropriate for this server to start.
     * @param args The arguments passed to the system on startup
     * @throws LifecycleException - This exception can be thrown by the server implementation on startup to indicate the reason for failure
     */
    public void start(String[] args) throws LifecycleException;

    /**
     * The callback invoked by Kiln when it is appropriate for this server to stop.
     *
     * @throws LifecycleException - This exception can be thrown by the server implementation on stop to indicate the reason for failure to stop.
     */
    public void stop() throws LifecycleException;

    /**
     * Verifies whether the given requirement is in the list of requirements of this server.
     * @param requested - The requested requirement
     * @return - True or False on whether the requested requirement is present in the required capabilities maintained by this server instance.
     */
    public default boolean hasRequirement(String requested){
        boolean retVal = false;
        if(getRequiredCapabilities().isPresent()){
            String[] requirements = getRequiredCapabilities().get();
            for (String requirement : requirements) {
                if(requirement.equalsIgnoreCase(requested)){
                    retVal = true;
                    break;
                }
            }

        }
        return retVal;

    }

    /**
     * Verifies whether the given set of available requires satisfies the ones which is required by this server instance
     * @param availableProvisions - The available provisions to check the requirements for
     * @return - True or False specifying whether the given list of available provisions satisfy the requirements.
     */
    public default boolean areRequirementsSatisfied(List<String> availableProvisions){
        boolean retVal = false;

        if(!getRequiredCapabilities().isPresent() || getRequiredCapabilities().get().length <=0){
            //no requirements hence satisfied by default
            retVal = true;
        }else{
            String[] requirements = getRequiredCapabilities().get();
            retVal = availableProvisions.containsAll(Arrays.asList(requirements));
        }

        return retVal;

    }
}
