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

package org.sysfoundry.kiln.base.util;

import java.util.*;

/**
 * Utility class used to provide convenient data structure creation and manipulation functions
 */
public class CollectionUtils {

    /**
     * Creates a key value tuple of the specified types
     * @param k - The string key
     * @param type - The type of the object
     * @param <T> - Retrieves the key value entry as a simple @See AbstractMap.SimpleEntry
     * @return
     */
    public static <T> AbstractMap.SimpleEntry<String,Class<? extends T>> tuple(String k, Class<? extends T> type){
        return new AbstractMap.SimpleEntry<>(k,type);
    }

    /**
     * Creates a key value of the specified key and value objects
     * @param k - The key of type String
     * @param v - The value of type Object
     * @return
     */
    public static AbstractMap.SimpleEntry<String,Object> KV(String k,Object v){
        return new AbstractMap.SimpleEntry<>(k,v);
    }

    /**
     * Creates a key value attribute of the specified key and value objects
     * @param key
     * @param value
     * @return
     */
    public static AbstractMap.SimpleEntry<String,String> attribute(String key, String value){
        return new AbstractMap.SimpleEntry<>(key,value);
    }

    /**
     * Convenience function to construct a Map instance with the given set of key value tuples
     * @param entries
     * @return
     */
    public static Map<String,Object> MAP(AbstractMap.SimpleEntry<String,Object>... entries){
        Map<String,Object> map = new HashMap<>();
        for (AbstractMap.SimpleEntry<String, Object> entry : entries) {
            map.put(entry.getKey(),entry.getValue());
        }
        return map;
    }

    /**
     * Convenience function to create a Set instance of the given type and instances
     * @param type - The datatype of the set
     * @param ts - The list of instances of the specified type
     * @param <T>
     * @return
     */
    public static <T> Set<T> SET(Class<T> type,T... ts){
        Set<T> newSet = new HashSet<>();
        newSet.addAll(Arrays.asList(ts));
        return newSet;
    }

    /**
     * Convenience function to create a List instance of the given type and instances
     * @param type - THe datatype of the list
     * @param ts - The list of instances of the specified type
     * @param <T>
     * @return
     */
    public static <T> List<T> LIST(Class<T> type,T... ts){
        List<T> newList = new ArrayList<>();
        newList.addAll(Arrays.asList(ts));
        return newList;
    }

}
