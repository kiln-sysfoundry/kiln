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
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsParser;

import java.io.IOException;
import java.util.*;

import static org.sysfoundry.kiln.base.util.JSONUtils.merge;

/**
 * A properties based configuration source.
 * This ConfigurationSource allows multiple properties instances to be merged and composed in to a
 * single logical one.
 * The order of merge is the reverse order of the input.
 * The first item in the list will be the last one merged and hence will have the opportunity to override / hide the other properties down in the list
 *
 */
public class PropertiesConfigurationSource implements ConfigurationSource{

    private JavaPropsFactory factory = new JavaPropsFactory();

    private JsonNode rootNode;

    private JavaPropsMapper mapper = new JavaPropsMapper();

    private Optional<List<Properties>> optionalPropertiesList = Optional.empty();

    /**
     * Constructs a PropertiesConfigurationSource from the given list of properties objects
     * @param propertiesList - The list of properties to be composed as a configuration source
     */
    public PropertiesConfigurationSource(Properties... propertiesList){
        List<Properties> propertiesListObj = Arrays.asList(propertiesList);
        optionalPropertiesList = Optional.ofNullable(propertiesListObj);
        rootNode = _initializeAndLoadProperties(this.optionalPropertiesList);

        //configure the mapper to not throw errors on unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    private JsonNode _initializeAndLoadProperties(Optional<List<Properties>> optionalPropertiesList) {
        JsonNode tempRootNode = null;

        if(optionalPropertiesList.isPresent()){
            List<Properties> listOfPropertiesObjs = optionalPropertiesList.get();
            //reverse the list
            Collections.reverse(listOfPropertiesObjs);

            for (Properties listOfPropertiesObj : listOfPropertiesObjs) {
                tempRootNode = _loadPropertiesNodes(listOfPropertiesObj,tempRootNode);
            }

        }
        return tempRootNode;
    }

    private JsonNode _loadPropertiesNodes(Properties listOfPropertiesObj, JsonNode inRootNode) {
        JavaPropsParser parser = factory.createParser(listOfPropertiesObj);

        try {
            JsonNode tempRootNode = mapper.readTree(parser);
            if(inRootNode == null){
                return tempRootNode;
            }else{
                //merge with the in coming root node and return it
                merge(tempRootNode,inRootNode,false);
                return inRootNode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return inRootNode;
        }
    }


    @Override
    public <T> T get(String path, Class<T> clz) throws ConfigurationException {
        if(!isValid(path)){
            throw new ConfigurationNotFoundException(String.format("No valid configuration value found for Path %s",path));
        }

        try {
            T value = mapper.treeToValue(rootNode.at(path), clz);
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
            return defaultVal;
        }
    }

    @Override
    public boolean isValid(String path) {
        JsonNode jsonNode = rootNode.at(path);
        return !jsonNode.isMissingNode();
    }
}
