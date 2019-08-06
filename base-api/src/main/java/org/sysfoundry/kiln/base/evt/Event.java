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
