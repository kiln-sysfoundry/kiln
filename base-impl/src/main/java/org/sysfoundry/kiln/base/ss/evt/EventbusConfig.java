package org.sysfoundry.kiln.base.ss.evt;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class EventbusConfig {

    @JsonProperty("async-executor-threads")
    private int asyncExecutorThreads;

}
