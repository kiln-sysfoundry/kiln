## Introduction
Welcome to the Kiln framework documentation site. 
Here you can find all information about the Kiln library as well as guides and examples to basic and advanced features of Kiln.

### What is Kiln?
Kiln is a modular lightweight java library and framework which can be used to develop applications on the JVM.
Kiln's goal is to provide a handful set of usefull abstractions which helps the developer develop Systems using the Java ecosystem.

### Why Kiln?
Why Kiln? You may ask. The key motivations of Kiln are,

1. To provide a simple and minimal library which encourages composition rather than a rigid framework.
2. To encourage thinking in modules as well as making the concept of Module formal and explicit rather than implicit.
3. To avoid code bloat.
4. To provide an alternatives to the other popular libraries.
5. To minimize the abstractions provided (unlike other popular libraries).
6. An experiment to build an alternative and thriving community to maintain and manage Kiln. 

### Key Concepts of Kiln
Kiln's key concepts are briefly discussed below.

1. System and Subsystems
2. Server and Lifecycle Events
3. Inter-Subsystem communication
4. Subsystem Configuration
5. System Configuration

### System and Subsystems

The core abstraction in Kiln is the concept of System and Subsystems.
The developer composes a System from a set of Subsystems (provided by Kiln or developed by him/her or 3rd party ones).
The below diagram depicts the core concepts of System and Subsystems in the context of Kiln.

<div style="display: flex; justify-content: center;">
  <img src="images/Sys_Subsys.png" alt="System and Subsystems" width="500" height="300"/>
</div>


#### Sys
A System in Kiln, also called as Sys, represents the composed system made up of a number of sub-systems represented by Subsys.

#### Subsys
A Subsystem in Kiln, also called as Subsys, represents a module in Kiln which provides a set of related features or capabilities to Kiln.
Kiln itself is built on these fundamental concepts which makes it a modular library.
Kiln achieves its modularity using the [Google's Guice DI library][guice]. 
Kiln also inherits the Dependency Injection and AOP features which are already available in [Guice][guice].
All Subsystems are valid [Guice][guice] Modules. Which allows the developer to mix and match plain [Guice][guice] Modules with Kiln's Subsystems.

A Subsys instance in Kiln can be visualized as below

<div style="display: flex; justify-content: center;">
<img src="images/Subsys_Abstraction_Encapsulation.png" alt="Subsys abstraction and encapsulation" width="1000" height="500"/>
</div>

A Sys thus composed of multiple Subsys and their relationships are illustrated below. 
To enable this composition and binding of service provision to service requirement, the Sys provides additional services as well. 

<div style="display: flex; justify-content: center;">
<img src="images/Sys_Subsys_Provides_Requires.png" alt="Sys , Subsys Requires Provides" width="1000" height="500"/>
</div>
 
### Server and Lifecycle Events
Though Sys and Subsys abstractions are sufficient to provide module and composition capabilities, a System is useful only if it successfully does something / provides a function.
Kiln's goal is not to be prescriptive on how or what the System does. However many Systems are useful as a service in its context, and such a service requires that, the system does something
when the System in question is started or stopped. 

To support such a notion, Kiln provides the abstraction of a Server.

A Server is any functionality which needs to be invoked during specific system lifecycle events.

The Sys provides well defined lifecycle events such as,

1. Initializing
2. Starting
3. Started
4. Stopping
5. Stopped
6. Start-Failed

Any Server implementation should provide a valid implementation of the start & stop methods (at least) to be invoked during the Starting & Stopping lifecycle events.
A server expresses its desire to be started and stopped by providing appropriate implementations of the respective methods.

#### Subsys and Server
A given Subsys can provide multiple such Server(s), and since a Sys can be composed of multiple such Servers, what is the order of invocation of these servers.
To be able to order the servers appropriately, the Server abstraction supports the notion of Server provisions and requirements.
A server can and should express explicitly its requirements as well as its provisions so that Kiln can appropriately order the server instances to accomodate the server requirements and provisions.
This notion of Requirements and Provisions provides a nice side effect that a Server instance need not be worried about the order in which it will be invoked.
The Kiln library automatically orders the services according to its requirements and provisions expressed.

This approach is adaptive in a System which is composed of multiple Subsystems.

Imagine the below list of Servers are provided by a combination of Subsystems in a given System, 

| Server Name       | Provides Capabilities | Requires Capabilities   |
|-------------------|-----------------------|-------------------------|
| Datasource Server | sql-datasource        |         - (means None)  | 
| Http Server       | http,https            |         * (means all)   |
| Messaging Server  | amqp,jms              |         sql-datasource  |
| Ftp Server        | ftp                   | sql-datasource,amqp,jms | 
 
  
Depending on the provides and requires capabilities specified by the server the startup order of these servers are depicted below.

| Server Name       | Why?                                                            |
|-------------------|-----------------------------------------------------------------|
| Datasource Server | Since it does not require any other capability                  | 
| Messaging Server  | Since it requires only the *sql-datasource* capability          |
| Ftp Server        | Since it requires *sql-datasource*, *amqp* & *jms* capabilities |
| Http Server       | Since it requires *all* the capabilities                        | 

#### Provides and Requires capabilities
A Server instance can express the capabilities it provides and requires as an Array of Strings.
For more details about the Provides and Requires capabilities refer to the Javadoc of the Server abstraction.

#### Sys, Subsys and Server
The diagram below illustrates the relationship that exists between Sys, Subsys and Server abstractions.

<div style="display: flex; justify-content: center;">
<img src="images/Sys_Subsys_Server_Relationship.png" alt="System, Subsystems and Servers Relationship" width="1000" height="400"/>
</div>

### Inter-subsystem Communication
Composing a System of multiple Subsystems is not sufficient, unless the Subsystems have the capability to interact / communicate with each other.
Kiln provides the notion of EventBus, which provides the capability for Subsystems to communicate between themselves through Events.

The EventBus provides the ability for Subsystems and its internals to be able to publish events both synchronously and asynchronously.
This capability is depicted below.

<div style="display: flex; justify-content: center;">
<img src="images/EventBus_Subsys_Relationship.png" alt="EventBus and Subsys Relationship" width="1000" height="600"/>
</div>

### Subsystem Configuration
In Kiln, each Subsystem supports a notion of Configuration called as Subsystem Configuration. This provides the capability for a developer 
to extract and parameterize certain Subsystem functionality. For example , a CurrencyRate Subsystem could support a configurable parameter called as **currency_code**.
If the Subsystem is designed in such a way that this configuration can be changed and 
parameterized based on the context of usage of this Subsystem, this allows other developers 
who are using this Subsystem in a different context than in which it was designed, to be able to change the **currency_code** without getting into the internals of the Subsystem.
This capability in Kiln is called as the Subsystem Configuration.

Subsystem Configuration is simply data file in the JSON format and stored in the same package as the Subsystem.

An example configuration is shown below.

```json
{
  "currency_code" : "INR",
  "currency_country" : "India"
}
```

### System Configuration
Kiln also provides the developer of the System to specify and override any Subsystem Configuration by providing one or more JSON configuration files
when wiring up the System. This allows the composer of the System to be able to adjust and tweak the behaviour of any Subsystem from *outside* without having to tinker with the internals of the Subsystem.

This configuration is also in the format JSON as similar to the Subsystem Configuration.

 



[guice]: https://github.com/google/guice