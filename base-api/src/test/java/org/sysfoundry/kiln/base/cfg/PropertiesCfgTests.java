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