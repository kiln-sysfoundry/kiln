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

import org.sysfoundry.kiln.base.Constants;
import org.sysfoundry.kiln.base.srv.Server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AboutSubsys {


    /**
     * Identifier of the Subsystem.
     * By default if no Identifier is provided by the implementer of the subsystem,
     * Kiln should assign the Type name of the Subsys. This is indicated by the default value of '__TYPE_NAME__'
     * @return The Identifier of the Subsystem
     */
    String id() default Constants.TYPE_NAME;

    /**
     * The Configuration class of the Subsystem
     * By default if no Configuration class is provided, the Subsystem is assumed to have a Void type. Which means no Configuration.
     * @return The Configuration class of the Subsystem
     */
    Class configType() default Void.class;

    /**
     * The Configuration prefix of the subsystem
     * @return The Prefix path of the configuration source where the configuration is to be loaded from.
     */
    String configPrefix() default Constants.EMPTY;

    /**
     * Specify whether to enable configuration validation. Default is disabled.
     * @return Retrieves whether the configuration validation is enabled or disabled.
     */
    boolean validateConfig() default false;

    /**
     * The Server classes provided by the Subsystem
     * Default is assumed to be empty. Which means no Server is provided by this subsystem
     * @return The List of Server Classes provided by the Subsystem
     */
    Class<? extends Server>[] servers() default {};

    /**
     * The Provisions of this Subsystem. A subsystem can document its provisions to the external world through this annotation.
     * @return The List of Key instances which indicate the Lookup key of the Provided service
     */
    Key[] provisions() default {};

    /**
     * The Requirements of this Subsystem. A Subsystem can document its requirements to the external world through this annotation.
     *
     * @return The list of Key instances which indicate the Lookup Key of the Required service(s)
     */
    Key[] requirements() default {};

    /**
     * A brief documentation about this Subsystem. Optionally this can also be a URL pointing to an external documentation.
     * @return The Brief documentation about this Subsystem.
     */
    String doc() default Constants.TO_BE_DONE;

    /**
     * A list of authors of this Subsystem.
     * @return The list of author email ids
     */
    String[] authors() default {};

    /**
     * The URL of the provider of this Subsystem.
     * @return The URL of the provider of this Subsystem.
     */
    String provider() default Constants.UNKNOWN;

    /**
     * A list of the names of events which this subsystem emits or publishes.
     * @return The Array of event names which this subsystem emits or publishes.
     */
    String[] emits() default {};

    /**
     * A list of names of events which this subsystem reacts to or subscribes for.
     *
     * @return The Array of event names which this subsystem reacts or subscribes for.
     */
    String[] reactsTo() default {};


}
