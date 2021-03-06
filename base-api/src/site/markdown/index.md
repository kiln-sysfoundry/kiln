# base-api
The base-api module provides the key interfaces and classes which serve as the core api for kiln.
Kiln subsystem modules are dependent on these core abstractions provided as part of this module
The base-api is made up of the below packages. Each of these packages and purpose are briefly described below.

## system
The system package (org.sysfoundry.kiln.base.sys) houses the key interfaces and classes which logically belong to the system concept in kiln.
The key interfaces & classes in this package are

1. Sys - The Sys interface represents a System composed by the developer using Kiln.
2. SysBuilder - The SysBuilder interface represents a System Builder which the developer uses to compose a system which he / she desires.
3. Subsys - The SubSys abstract class represents an abstraction of a Subsystem in kiln. All subsystem implementations in kiln are expected to extend from this abstract class.
4. SubsysInfo - The SubsysInfo class represents a Subsystem's or System's attributes. This is provided as a capability to self document the system or subsystems created using kiln.

In addition to the above, this package also houses the below key @Qualifier annotations. These are meant for subsystems and 
consumers of the subsystems to register and lookup key object instances or data structures within the system package.

The key @Qualifier annotations and their purpose are discussed below

1. SubsysSet - This annotation is used by kiln to register all the subsystems in the System. A Set&lt;Subsys&gt; object is bound to this annotation and allows any other subsystem using kiln to lookup these set of subsystems.
2. SubsysConfigSourceSet - This annotation is used by kiln to register all the subsystem's configuration in the System. In Kiln, a subsystem can and should have a default set of configurations which it can load in the absence of an external configuration. Anyone looking to get this set of all Subsystem's default configuration can look up this Set&lt;ConfigurationSource&gt; object.
3. SysConfigSourceSet - This annotation is used by kiln to register all the system configuration provided by the composer / developer of the system during the System Build phase (using any SysBuilder implementation). Similar to the SubsystemConfigSourceSet, this is also a Set&lt;ConfigurationSource&gt; instance.
4. SysConfigSource - This annotation is used by kiln to register the composition of the Subsystem configuration and the System configuration in to a single ConfigurationSource instance.
5. RuntimeSysConfigSource - This annotation is used by kiln to register the System Properties as a ConfigurationSource

A conceptual view of the key abstraction and their relationships are depicted in the below diagram.

<div style="display: flex; justify-content: center;">
  <img src="images/Sys-Class-diagram-cropped.svg" alt="system package key entities and relationship"/>
</div>


A conceptual view of all the key components which makeup the system package is shown below.

<div style="display: flex; justify-content: center;">
  <img src="images/Sys-Subsys-Composition.svg" alt="System and Subsystem composition"/>
</div>

## server
The server package (org.sysfoundry.kiln.base.srv) houses the key interfaces, classes and annotations which logically belong to the server concept in kiln.
The key interfaces, classes and annotations in this package are,

1. Server - The Server interface is the abstraction which represents a Server in kiln.
2. AbstractServer - This is the abstract class which provides an easy to use base class for Server Implementations.
3. ServerSet - The set of all servers registered by all subsystems (and plain guice modules) is bound to this Annotations. This annotation can be used by users of kiln to lookup the set of all servers registered in the system.

## Eventing
The eventing package (org.sysfoundry.kiln.base.evt) houses the key interfaces, classes and annotations which logically belong to the eventing concept in kiln.
The key interfaces, classes and annotations in this package are,

1.Event - The Event value object represents an immutable Event object in kiln. Any subsystem which wants to interact with other systems purely through the Message (event) processing model can use this abstraction to interact and communicate with other subsystems.
2.EventBus - The EventBus interface provides the abstraction of an event bus which allows participating Subsystems and its internals to publish and subscribe to interested events. The EventBus supports the notion of synchronous and asynchronous events.
3.OnEvent - The OnEvent annotation provides the ability for interested classes (singletons) in Kiln to express the callback on interested events

<div style="display: flex; justify-content: center;">
  <img src="images/Event_publish_subscribe_receive_events.svg" alt="Publish, Subscribe and Receiving Events"/>
</div>

Note: In the diagram above, the objects / subscribers of events need not explicitly register with the EventBus, this is accomplished by annotating an appropriate
callback method with the OnEvent annotation.

## Configuration
The configuration package (org.sysfoundry.kiln.base.cfg) houses the key interfaces, classes and annotations which logically belong to the configuration concept in kiln.
The key interfaces, classes and annotations in this package are,

1. ConfigurationSource - This interface represents the Configuration Source abstraction in kiln. All configuration sources need to implement this interface.
2. InputStreamConfigurationSource - This concrete class provides the ability to merge a list of inputstreams representing the configurations (in JSON) format into a single configuration source.
3. CompositeConfigurationSource - This concrete class composes multiple configuration sources into a composite one. It provides the ability to designate a set of configuration as primary and secondary so as to control the order of configuration lookup.
4. PropertiesConfigurationSource - This concrete class turns a java.util.Properties data-structure in to a configuration source.

The PropertiesConfigurationSource is used by Kiln to turn the System properties into a nested hierarchical tree structure similar to JSON format.

## Util
The Util package (org.sysfoundry.kiln.base.util) provides a number of utility classes and functions which is used in kiln all across.


