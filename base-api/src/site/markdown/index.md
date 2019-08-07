# base-api
The base-api module provides the key interfaces and classes which serve as the api for kiln users.
Kiln subsystem modules are dependent on these core abstractions provided as part of this module
The base-api is made up of the below packages. Each of these packages and purpose are briefly described below

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

A conceptual view of the key abstraction and their relationships are depicted in the below diagram.

<div style="display: flex; justify-content: center;">
  <img src="images/Sys-Class-diagram-cropped.svg" alt="System and Subsystems"/>
</div>


A conceptual view of all the key components which makeup the system package is shown below.

<div style="display: flex; justify-content: center;">
  <img src="images/Sys-Subsys-Composition.svg" alt="System and Subsystems"/>
</div>
