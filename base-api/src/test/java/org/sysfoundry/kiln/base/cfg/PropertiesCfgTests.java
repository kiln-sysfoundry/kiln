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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertiesCfgTests {

    @Test
    public void systemPropertiesTest() throws IOException, ConfigurationException {
        Properties properties = new Properties();
        properties.load(PropertiesCfgTests.class.getResourceAsStream("/test-props.properties"));
        PropertiesConfigurationSource configurationSource = new PropertiesConfigurationSource(System.getProperties(),properties);

        //User user = configurationSource.get("/user", User.class);

        Map values = configurationSource.get("/countries",Map.class);

        System.out.println("Country Details "+values);

    }

}

@Data
class Countries{
    Map<String,String> values;
}

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
class User{
    @JsonProperty("dir")
    String directory;

    @JsonProperty("home")
    String home;

    @JsonProperty("name")
    String name;

    String country;
}