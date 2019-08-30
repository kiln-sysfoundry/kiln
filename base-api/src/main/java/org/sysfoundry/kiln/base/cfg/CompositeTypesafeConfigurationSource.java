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
import com.typesafe.config.ConfigFactory;
import lombok.NonNull;
import org.slf4j.Logger;
import org.sysfoundry.kiln.base.health.Log;

import java.util.Set;

import static org.sysfoundry.kiln.base.sys.Subsys.BASE_SUBSYS_NAME;

public class CompositeTypesafeConfigurationSource implements ConfigurationSource{

    private Config compositeConfig;

    private static final Logger log = Log.get(BASE_SUBSYS_NAME);


    public CompositeTypesafeConfigurationSource(@NonNull ConfigurationSource overridingConfigSource,
                                                @NonNull Set<ConfigurationSource> secondaryConfigurationSources,
                                                @NonNull Set<ConfigurationSource> primaryConfigurationSources
                                                ){
        Config finalConfig = ConfigFactory.empty();

        try {
            Config overridingConfig = overridingConfigSource.getView(Config.class);


            if(overridingConfig != null){
                finalConfig = finalConfig.withFallback(overridingConfig);
            }

            for (ConfigurationSource configurationSource : primaryConfigurationSources) {
                Config tempConfig = configurationSource.getView(Config.class);
                if(tempConfig != null){
                    finalConfig = finalConfig.withFallback(tempConfig);
                }
            }

            for (ConfigurationSource configurationSource : secondaryConfigurationSources) {
                Config tempConfig = configurationSource.getView(Config.class);
                if(tempConfig != null){
                    finalConfig = finalConfig.withFallback(tempConfig);
                }
            }


        } catch (ConfigurationException e) {
            //e.printStackTrace();
            log.warn("Error while loading configuration {}",e.getMessage());
        }

        this.compositeConfig = finalConfig;

    }

    @Override
    public <T> T get(String path, Class<T> clz) throws ConfigurationException {
        return ConfigBeanFactory.create(compositeConfig.getConfig(path),clz);
    }

    @Override
    public <T> T get(String path, Class<T> clz, T defaultVal) {
        if(compositeConfig.hasPath(path)){

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

        return compositeConfig.hasPath(path);
    }

    @Override
    public <T> T getView(Class<T> viewType) throws ConfigurationException {
        if(viewType.isAssignableFrom(Config.class)){
            return (T)compositeConfig;
        }
        throw new ConfigurationException(String.format(unSupportedViewTypeMessageFormat,viewType));
    }
}
