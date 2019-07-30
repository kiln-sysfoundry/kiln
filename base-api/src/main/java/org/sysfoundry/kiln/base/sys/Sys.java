package org.sysfoundry.kiln.base.sys;

import org.sysfoundry.kiln.base.LifecycleException;

/**
 * Abstraction of a System in Molecule.
 *
 */
public interface Sys {


    public static final String INITIALIZING_EVENT = "sys-initializing";
    public static final String STARTING_EVENT = "sys-starting";
    public static final String STARTED_EVENT = "sys-started";
    public static final String STOPPING_EVENT = "sys-stopping";
    public static final String STOPPED_EVENT = "sys-stopped";
    public static final String VALIDATING_STATUS_EVENT = "sys-validating-status";
    public static final String START_FAILED_EVENT = "sys-start-failed";

    /**
     * Method invoked by  the caller to start the 'system'
     * Note that the start method could be synchronous (wait for the system and all services to be started before returning) or
     * could be asynchronous (initiate the start and return immediately).
     *
     * @throws LifecycleException Exception thrown when errors occur during system startup
     */
    void start() throws LifecycleException;

    /**
     * Retrieves whether the Sys is started or not
     * @return Flag indicating the started status of a Sys
     */
    boolean isStarted();

    /**
     * Issues a stop signal when the Sys needs to be stopped
     * Note that similar to the start() method, the behaviour (synchronous or asynchronous) depends on the
     *
     */
    void stop();
}
