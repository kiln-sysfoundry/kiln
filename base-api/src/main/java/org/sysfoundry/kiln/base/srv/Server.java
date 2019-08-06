package org.sysfoundry.kiln.base.srv;

import org.sysfoundry.kiln.base.LifecycleException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public interface Server {

    public static final int ORPHAN_LEVEL = -2;
    public static final int UNKNOWN_LEVEL = -1;

    public String getName();

    public Integer getStartLevel();

    public void setStartLevel(Integer startLevel);

    public default Optional<String[]> getRequiredCapabilities(){
        return Optional.of(new String[]{});
    }

    public default Optional<String[]> getProvidedCapabilities(){
        return Optional.of(new String[]{});
    }

    public void start(String[] args) throws LifecycleException;

    public void stop() throws LifecycleException;

    public default boolean hasRequirement(String requested){
        boolean retVal = false;
        if(getRequiredCapabilities().isPresent()){
            String[] requirements = getRequiredCapabilities().get();
            for (String requirement : requirements) {
                if(requirement.equalsIgnoreCase(requested)){
                    retVal = true;
                    break;
                }
            }

        }
        return retVal;

    }

    public default boolean areRequirementsSatisfied(List<String> availableProvisions){
        boolean retVal = false;

        if(!getRequiredCapabilities().isPresent() || getRequiredCapabilities().get().length <=0){
            //no requirements hence satisfied by default
            retVal = true;
        }else{
            String[] requirements = getRequiredCapabilities().get();
            retVal = availableProvisions.containsAll(Arrays.asList(requirements));
        }

        return retVal;

    }
}
