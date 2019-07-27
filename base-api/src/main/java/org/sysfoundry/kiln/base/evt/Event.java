package org.sysfoundry.kiln.base.evt;

import com.github.krukow.clj_lang.PersistentHashMap;
import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Value
public class Event {

    private final UUID id = UUID.randomUUID();
    private String name;
    private Instant time;
    private Map<String,Object> data;


    public static Event create(String name,Map<String,Object> data){
        return new Event(name,Instant.now(),data);
    }

    public static Event create(String name){
        return create(name, PersistentHashMap.emptyMap());
    }

}
