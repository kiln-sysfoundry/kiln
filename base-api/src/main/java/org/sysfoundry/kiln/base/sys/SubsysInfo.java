package org.sysfoundry.kiln.base.sys;

import java.util.Map;
import java.util.Objects;

public class SubsysInfo {

    private String id;
    private Map<String,Object> attributes;

    public SubsysInfo(String id, Map<String,Object> attributes){
        this.id = id;
        this.attributes = attributes;
    }

    public String getID() {
        return id;
    }

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
        return "ModuleInfo{" +
                "id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
