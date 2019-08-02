## Introduction
Welcome to the Kiln framework documentation site. 
Here you can find all information about the Kiln Framework as well as guides and examples to basic and advanced features of Kiln.

### What is Kiln?
Kiln is a modular lightweight java library and framework which can be used to develop applications on the JVM.
The goal of Kiln is to be a kernel of a System and as such aims to provide very basic features which are required for any system.
Using Kiln, a developer can develop any sort of System (whether server, console or interactive ones).
Moreover he or she can also develop another framework on top of the basic features provided by Kiln.

### Key Concepts of Kiln
Kiln's key concepts are briefly discussed below.

1. System and Subsystems
2. Server and ServerLifecycle
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

The relationship between Sys & Subsys is illustrated below.

<div style="display: flex; justify-content: center;">
<img src="images/Sys_Subsys_Relationship_UML.png" alt="System and Subsystems Relationship" width="850" height="200"/>
</div>

 



[guice]: https://github.com/google/guice