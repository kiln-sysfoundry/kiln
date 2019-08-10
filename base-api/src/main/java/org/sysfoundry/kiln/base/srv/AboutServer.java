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

package org.sysfoundry.kiln.base.srv;

import org.sysfoundry.kiln.base.Constants;
import org.sysfoundry.kiln.base.sys.Key;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AboutServer {


    /**
     * Identifier of the Server.
     * By default if no Identifier is provided by the implementer of the subsystem,
     * Kiln should assign the Type name of the Subsys. This is indicated by the default value of '__TYPE_NAME__'
     * @return The Identifier of the Subsystem
     */
    String name() default Constants.TYPE_NAME;

    /**
     * The Provisions of this Server. A Server can document its provisions to the external world through this annotation.
     * @return The List of Capability names which are provided by this server
     */
    String[] provides() default {};

    /**
     * The Requirements of this Server. A Server can document its requirements to the external world through this annotation.
     *
     * @return The list of Capability names which are required by this server before it can start
     */
    String[] requires() default {};

    /**
     * A brief documentation about this Server. Optionally this can also be a URL pointing to an external documentation.
     * @return The Brief documentation about this Server.
     */
    String doc() default Constants.TO_BE_DONE;

}
