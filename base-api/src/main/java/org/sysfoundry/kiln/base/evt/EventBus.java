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

/**
 * This class represents the EventBus abstraction in Kiln.
 * The EventBus is a singleton instance in kiln which has the ability to publish events in synchronous and asynchronous manner.
 * The EventBus also provides the ability for interested subscribers (other objects) in the system to subscribe for events
 * Similarly the subscribers can also unsubscribe.
 */
public interface EventBus {

    /**
     * Publish the given message synchronously
     * @param message - The message to publish
     */
    void publishSync(Object message);

    /**
     * Publish the given message asynchronously
     * @param message - The message to publish
     */
    void publishASync(Object message);


    /**
     * Register for interested events
     * @param subscriber - The subscriber who wants to be registered for events
     */
    void register(Object subscriber);

    /**
     * Unregister an registered subscriber
     * @param subscriber - The subscriber who wants to be unregistered from the event bus
     */
    void unRegister(Object subscriber);


}
