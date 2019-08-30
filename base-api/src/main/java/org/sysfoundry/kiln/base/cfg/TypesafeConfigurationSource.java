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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;

public class TypesafeConfigurationSource implements ConfigurationSource{

    private Config configurationObject;

    public TypesafeConfigurationSource(Config config){
        this.configurationObject = config;
    }

    @Override
    public <T> T get(String path, Class<T> clz) throws ConfigurationException {
        return ConfigBeanFactory.create(configurationObject.getConfig(path),clz);
    }

    @Override
    public <T> T get(String path, Class<T> clz, T defaultVal) {
        if(configurationObject.hasPath(path)){

            try {
                return get(path,clz);
            } catch (ConfigurationException e) {
                return defaultVal;
            }
        }else{
            return defaultVal;
        }

    }

    @Override
    public boolean isValid(String path) {
        return configurationObject.hasPath(path);
    }

    @Override
    public <T> T getView(Class<T> viewType) throws ConfigurationException {
        if(viewType.isAssignableFrom(Config.class)){
            return (T)configurationObject;
        }
        throw new ConfigurationException(String.format(unSupportedViewTypeMessageFormat,viewType));
    }

}
