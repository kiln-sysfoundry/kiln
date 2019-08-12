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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.util.JSONUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class OverridingCompositeConfigurationSource implements ConfigurationSource{

    private Set<ConfigurationSource> configurationSources;
    private JsonNode rootNode;
    protected ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public OverridingCompositeConfigurationSource(Set<ConfigurationSource> secondaryConfigurationSources,
                                                  Set<ConfigurationSource> primaryConfigurationSources){
        this(null,secondaryConfigurationSources,primaryConfigurationSources);
    }

    public OverridingCompositeConfigurationSource(ConfigurationSource overridingConfigurationSource,
                                                  Set<ConfigurationSource> secondaryConfigurationSources,
                                                  Set<ConfigurationSource> primaryConfigurationSources){
        this(overridingConfigurationSource,secondaryConfigurationSources,primaryConfigurationSources,false);
    }

    public OverridingCompositeConfigurationSource(ConfigurationSource overridingConfigurationSource,
                                                  Set<ConfigurationSource> secondaryConfigurationSources,
                                                  Set<ConfigurationSource> primaryConfigurationSources,boolean throwErrorOnMismatchedTypes){
        Set<ConfigurationSource> mergedSources = new LinkedHashSet<>();
        mergedSources.addAll(secondaryConfigurationSources);
        mergedSources.addAll(primaryConfigurationSources);
        if(overridingConfigurationSource != null) {
            //add the overridingConfigurationSource as the first in the list so it is given priority over the rest
            mergedSources.add(overridingConfigurationSource);
        }
        this.configurationSources = mergedSources;

        rootNode = constructMergedRootNode(throwErrorOnMismatchedTypes);

    }

    private JsonNode constructMergedRootNode(boolean throwErrorOnMismatchedTypes) {
        JsonNode configRootNode = null;

        for (ConfigurationSource configurationSource : this.configurationSources) {
            try {
                JsonNode newNode = configurationSource.getView(JsonNode.class);
                if(configRootNode == null){
                    configRootNode = newNode;
                }else{
                    JSONUtils.merge(newNode,configRootNode,throwErrorOnMismatchedTypes);
                }

            } catch (ConfigurationException e) {
                String message = String.format("Failed to get Json view of configuration source %s. So this source will be ignored from the merged final configuration",
                        configurationSource);
                log.warn(message);
            }
        }


        checkArgument(configRootNode != null,"Configuration Source is in an invalid state. No Valid configuration root node found!");


        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        return configRootNode;
    }

    @Override
    public <T> T get(String path, Class<T> clz)throws ConfigurationException {
        if(!isValid(path)){
            throw new ConfigurationNotFoundException(String.format("No valid configuration value found for Path %s",path));
        }

        try {
            T value = OBJECT_MAPPER.treeToValue(rootNode.at(path), clz);
            return value;
        } catch (JsonProcessingException e) {

            throw new ConfigurationTypeException(String.format("Error occurred during type mapping of config path %s to type %s",path,clz),e);
        }
    }


    @Override
    public <T> T get(String path, Class<T> clz, T defaultVal) {
        try {
            T val = get(path, clz);
            return val;
        } catch (ConfigurationException e) {
            log.trace("Unable to load configuration for path {}, due to exception {} so returning the default value {}",
                    path,e.getMessage(),defaultVal);
            return defaultVal;
        }
    }

    @Override
    public boolean isValid(String path) {

        JsonNode jsonNode = rootNode.at(path);
        return !jsonNode.isMissingNode();

    }

    @Override
    public <T> T update(@NonNull String path, @NonNull T objTobeUpdated) throws ConfigurationException {
        if(!isValid(path)){
            throw new ConfigurationNotFoundException(String.format("No valid configuration value found for Path %s",path));
        }

        ObjectReader readerForUpdating = OBJECT_MAPPER.readerForUpdating(objTobeUpdated);
        JsonNode jsonNodeAtPath = rootNode.at(path);
        try {
            Object updatedValue = readerForUpdating.readValue(jsonNodeAtPath);
            return (T)updatedValue;
        } catch (IOException e) {
            throw new ConfigurationTypeException(String.format("Error occurred during type mapping of config path %s to object %s",path,objTobeUpdated),e);
        }
    }

    @Override
    public <T> T getView(@NonNull Class<T> viewType) throws ConfigurationException {
        if(viewType.isAssignableFrom(JsonNode.class)){
            //just return the internal JsonNode object
            return (T)this.rootNode;
        }
        throw new ConfigurationException(String.format(unSupportedViewTypeMessageFormat,viewType));

    }
}
