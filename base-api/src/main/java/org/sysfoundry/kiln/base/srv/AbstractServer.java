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

import java.util.Optional;

/**
 * The AbstractServer provides a convenient and easy to use abstract class for Server implementations to extend from
 * It provides the below capabilities for subclasses to reuse
 * <ol>
 *     <li>name - Ability to specify a name for the server</li>
 *     <li>providedCapabilities - Ability to specify a list of capabilities which are provided by the server</li>
 *     <li>requiredCapabilities - Ability to specify a list of capabilities which are required by the server in order to start successfully</li>
 *     <li>startLevel - Ability for kiln to set and retrieve the startlevel of the server</li>
 * </ol>
 */
public abstract class AbstractServer implements Server{

    private String name;
    private Optional<String[]> providesOptional = Optional.empty();
    private Optional<String[]> requiresOptional = Optional.empty();
    private Integer startLevel = Server.UNKNOWN_LEVEL;

    public AbstractServer(String name,String providesList,String requiresList){
        this.name = name;
        if(providesList!= null && !providesList.trim().isEmpty()){
            this.providesOptional = Optional.ofNullable(providesList.split(","));
        }
        if(requiresList!= null && !requiresList.trim().isEmpty()){
            this.requiresOptional = Optional.ofNullable(requiresList.split(","));
        }

    }

    public AbstractServer(String name,String[] provides,String[] requires){
        this.name = name;
        this.providesOptional = Optional.ofNullable(provides);
        this.requiresOptional = Optional.ofNullable(requires);
    }

    public AbstractServer(String name){
        this.name = name;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getStartLevel() {
        return startLevel;
    }

    @Override
    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
    }

    @Override
    public Optional<String[]> getRequiredCapabilities() {
        return requiresOptional;
    }

    @Override
    public Optional<String[]> getProvidedCapabilities() {
        return providesOptional;
    }
}
