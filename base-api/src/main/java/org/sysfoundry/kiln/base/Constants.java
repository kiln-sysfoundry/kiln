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

package org.sysfoundry.kiln.base;

public final class Constants {

    public static final String TYPE_NAME = "__TYPE_NAME__";
    public static final String TO_BE_DONE = "__TO_BE_DONE__";
    public static final String UNKNOWN = "__UNKNOWN__";

    public static final String ID = "__ID__";
    public static final String CONFIG_CLASS = "__CONFIG_CLASS__";
    public static final String CONFIG_PREFIX = "__CONFIG_PREFIX__";
    public static final String SERVER_CLASSES = "__SERVER_CLASSES__";
    public static final String PROVISIONS = "__PROVISIONS__";
    public static final String REQUIREMENTS = "__REQUIREMENTS__";
    public static final String DOCUMENTATION = "__DOCUMENTATION__";
    public static final String AUTHORS = "__AUTHORS__";
    public static final String PROVIDER = "__PROVIDER__";
    public static final String EMITS_EVENTS = "__EMITS_EVENTS__";
    public static final String REACTS_TO_EVENTS = "__REACTS_TO_EVENTS__";
    public static final String EMPTY = "__EMPTY__";
    public static final String VALIDATE_CONFIG = "__VALIDATE_CONFIG__";


    public static final String KILN_PROVIDER_URL = "url://www.sysfoundry.org";


    public static final String SERVER_LIFECYCLE_EVENTS = "events:<server>_<lifecycle-state>_events";
    public static final String NONE = "__NONE__";

    public static final String LOG_NAME_PATTERN = "[a-z0-9\\-]+";

    public static final class Authors{
        public static final String KILN_TEAM = "email:kiln.sysfoundry@gmail.com";
    }

    private Constants(){}


}
