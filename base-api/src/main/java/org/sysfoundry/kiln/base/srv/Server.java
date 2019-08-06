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


public interface Server {

    public static final int ORPHAN_LEVEL = -2;
    public static final int UNKNOWN_LEVEL = -1;

    public String getName();

    public Integer getStartLevel();

    public void setStartLevel(Integer startLevel);

    public default Optional<String[]> getRequiredCapabilities(){
        return Optional.of(new String[]{});
    }

    public default Optional<String[]> getProvidedCapabilities(){
        return Optional.of(new String[]{});
    }

    public void start(String[] args) throws LifecycleException;

    public void stop() throws LifecycleException;

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
