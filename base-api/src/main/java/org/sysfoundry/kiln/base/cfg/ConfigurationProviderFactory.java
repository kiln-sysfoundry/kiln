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

import lombok.extern.slf4j.Slf4j;
import org.sysfoundry.kiln.base.sys.SysConfigSource;

import javax.inject.Inject;
import javax.inject.Provider;

@Slf4j
public class ConfigurationProviderFactory<T> {

    private ConfigurationSource configurationSource;

    @Inject
    public void setConfigurationSource(@SysConfigSource ConfigurationSource configurationSource){
        this.configurationSource = configurationSource;
    }

    public Provider<T> getConfigurationProvider(String path,Class clz){
        return new Provider<T>() {
            @Override
            public T get() {
                try {
                    log.trace("Getting configuration instance for path {} and class {}",path,clz);
                    Object defaultInstance = clz.newInstance();
                    T configValue = (T)configurationSource.get(path, clz, defaultInstance);
                    return configValue;

                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    public Provider<T> getConfigurationProvider(String path,T defaultValue){
        return new Provider<T>() {
            @Override
            public T get() {
                log.trace("Getting configuration instance for path {}, with default value {}",path,defaultValue);
                T configValue = configurationSource.get(path, (Class<T>) defaultValue.getClass(), defaultValue);
                return configValue;
            }
        };
    }


}
