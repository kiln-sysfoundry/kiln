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

package org.sysfoundry.kiln.base.sys;

import java.util.Map;
import java.util.Objects;

/**
 * The SubsysInfo is used to capture the Subsys instance information and attributes
 */
public class SubsysInfo {

    private String id;
    private Map<String,Object> attributes;

    /**
     * Construct a SubsysInfo with the given id and attributes
     * @param id - The id of the subsys
     * @param attributes - The key value map of attributes of the subsys
     */
    public SubsysInfo(String id, Map<String,Object> attributes){
        this.id = id;
        this.attributes = attributes;
    }

    /**
     * The ID of the subsys
     * @return The ID of the subsys
     */
    public String getID() {
        return id;
    }

    /**
     * Retrieve the Attributes of the Subsys
     * @return The attribtues of the Subsys
     */
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubsysInfo)) return false;
        SubsysInfo that = (SubsysInfo) o;
        return id.equals(that.id) &&
                Objects.equals(getAttributes(), that.getAttributes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getAttributes());
    }

    @Override
    public String toString() {
        return "SubsysInfo{" +
                "id='" + id + '\'' +
                '}';
    }
}
