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

import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InputStream-Configuration-Tests")
public class InputStreamConfigurationSourceTests {

    @DisplayName("test-simple-attributes-update")
    @Test
    public void testSimpleAttributesUpdate() throws ConfigurationException {
        InputStreamConfigurationSource configurationSource = new InputStreamConfigurationSource(false,
                false,this.getClass().getResourceAsStream("/input-stream-example1.json"));

        Address address = new Address();

        System.out.println("Before "+address);

        Address updatedAddress = configurationSource.update("", address);
        System.out.println("After "+updatedAddress);
        System.out.println("After Original address "+address);

    }
}

@Data
class Address{
    private String name;
    private String email;
    private Integer age;
    private String country;
    private String city;
    private String continent;
}