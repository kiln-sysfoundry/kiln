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

package org.sysfoundry.kiln.base.cfg;

import lombok.NonNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This configuration source implementation provides the ability to compose multiple configuration sources in to a
 * single logical source
 */
public class CompositeConfigurationSource implements ConfigurationSource{

    private Set<ConfigurationSource> configurationSources;

    /**
     * Constructs a CompositeConfigurationSource from a 2 sets of configuration sources.
     *
     * @param secondaryConfigurationSources - The Secondary configuration sources.
     * @param primaryConfigurationSources - The Primary configuration sources
     */
    public CompositeConfigurationSource(Set<ConfigurationSource> secondaryConfigurationSources,
                                        Set<ConfigurationSource> primaryConfigurationSources){
        Set<ConfigurationSource> mergedSources = new LinkedHashSet<>();
        mergedSources.addAll(primaryConfigurationSources);
        mergedSources.addAll(secondaryConfigurationSources);
        this.configurationSources = mergedSources;
    }

    /**
     * Constructs a CompositeConfigurationSource from 3 sets of configuration sources
     * @param overridingConfigurationSource - The overrdingConfigurationSource which overrides over the primary and the secondary
     * @param secondaryConfigurationSources - The Secondary configuration source
     * @param primaryConfigurationSources - The primary configuration source
     */
    public CompositeConfigurationSource(ConfigurationSource overridingConfigurationSource,
                                        Set<ConfigurationSource> secondaryConfigurationSources,
                                        Set<ConfigurationSource> primaryConfigurationSources){
        Set<ConfigurationSource> mergedSources = new LinkedHashSet<>();
        //add the overridingConfigurationSource as the first in the list so it is given priority over the rest
        mergedSources.add(overridingConfigurationSource);
        mergedSources.addAll(primaryConfigurationSources);
        mergedSources.addAll(secondaryConfigurationSources);
        this.configurationSources = mergedSources;
    }

    /**
     * Constructs a CompositeConfigurationSource from the given set of configurationsources.
     * @param configurationSources - The set of configuration sources to be used.
     */
    public CompositeConfigurationSource(Set<ConfigurationSource> configurationSources){
        this.configurationSources = configurationSources;
    }

    /**
     * Retrieves the configuration from the given path, the retrieved data / configuration is retrieved in the specified Type
     * @param path A '/' separated path to the configuration
     * @param clz The Class of the configuration information
     * @param <T> The datatype of the configuration
     * @return
     * @throws ConfigurationException
     */
    @Override
    public <T> T get(String path, Class<T> clz) throws ConfigurationException {
        for (ConfigurationSource configurationSource : configurationSources) {
            if(configurationSource.isValid(path)){
                return configurationSource.get(path,clz);
            }
        }

        throw new ConfigurationNotFoundException(
                String.format("Could not find the configuration for path %s in any of the configured configuration sources %s",path,configurationSources));
    }

    /**
     * Retrieves the configuration from the given path, the retrieved data / configuration is retrieved in the specified type.
     * In case the specified configuration is not found, the supplied default value is returned
     * @param path A '/' separated path to the configuration
     * @param clz The Class of the configuration information
     * @param defaultVal The default value to be returned in the absence of the configuration
     * @param <T> The configuration in the form of the specified Type
     * @return
     */
    @Override
    public <T> T get(String path, Class<T> clz, T defaultVal) {
        try {
            return get(path,clz);
        } catch (ConfigurationException e) {
            return defaultVal;
        }

    }

    /**
     * Checks whether the given '/' separated path is a valid one within the configuration source.
     * @param path A '/' separated path to the configuration
     * @return
     */
    @Override
    public boolean isValid(String path) {
        boolean retVal = false;

        for (ConfigurationSource configurationSource : configurationSources) {
            if(configurationSource.isValid(path)){
                retVal = true;
                break;
            }
        }

        return retVal;
    }

    @Override
    public <T> T update(@NonNull String path,@NonNull T objTobeUpdated) throws ConfigurationException {
        for (ConfigurationSource configurationSource : configurationSources) {
            if(configurationSource.isValid(path)){
                return configurationSource.update(path,objTobeUpdated);
            }
        }

        throw new ConfigurationNotFoundException(
                String.format("Could not find the configuration for path %s in any of the configured configuration sources %s",path,configurationSources));
    }
}
