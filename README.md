## Remote Procedure Call ##

**Remote Procedure Call** is a Netty 4-based Remote Procedure Call (RPC) system.

This is my first GitHub project for the Spirit of Open Source. I has rewritten the source codes and completed the new features. Developing a simple Peer-to-peer framework based on RPC framework is my next plan in the future.


### Links ###

[Netty 4-based RPC System Development](http://www.slideshare.net/AllanHuang/netty-4-based-rpc-system-development) on Slideshare website.

### System Features ###

- NIO-based client and server with TCP socket
- Many-to-many relationship among servers and clients with multi-channels
- Parameters marshalling and unmarshalling by JSON
- Idle channels detection
- Inactive channels reconnection
- Cross-platform remote invocation by JSON and TCP socket
- Stream compresses and decompresses 
- High availability support
- Load balancing algorithms: Round-Robin and Low-Workload-First

### System Requirements ###

- Java Development Kit (JDK) 1.7.x or later stable version.

### Library Dependencies ###

- [Netty 4.0.27](http://netty.io/index.html) or later stable version except Netty 5.0.
- [Google Gson 2.3.1](https://code.google.com/p/google-gson/) or later stable version.
- [Apache Commons Lang 3.3.2](http://commons.apache.org/proper/commons-lang/index.html) or later stable version.
- [Apache Commons Configuration 1.10](https://commons.apache.org/proper/commons-configuration/index.html) or later stable version.
- [SLF4j 1.7.12](http://www.slf4j.org/) or later stable version.
- [Log4j 2.2](http://logging.apache.org/log4j/2.x/) or later stable version.
- [JZlib 1.1.3](http://www.jcraft.com/jzlib/) or later stable version.
- [JUnit 4.1.2](http://junit.org/) or later stable version.

### JAR File Dependencies ###
- netty-all-4.0.27.Final.jar
- gson-2.3.1.jar
- commons-lang3-3.3.2.jar
- commons-configuration-1.10.jar
- slf4j-api-1.7.12.jar
- log4j-api-2.2.jar
- log4j-core-2.2.jar
- log4j-jcl-2.2.jar
- log4j-slf4j-impl-2.2.jar
- jzlib-1.1.3.jar
- junit-4.12.jar
- hamcrest-core-1.3.jar

### Get Started ###
- Please refer to three classes defined in the `test.tw.me.ychuang.rpc` package: `BizServiceSkeleton`, `BizServiceStub`, and `BizServiceTest` classes separately.

- Starting up the client or/and server processes that is dependent on rpc-client.properties or/and rpc-server.properties whether are found in the classpath.   

- `ClientChannelManager` in the `tw.me.ychuang.rpc` package provides several convenient methods for management, e.g. `pauseChannelProxies(serverHost, serverPort)`, `stopChannelProxies(serverHost, serverPort)`, and `restartChannelProxies(serverHost, serverPort)` methods.

### License ###

**Remote Procedure Call** is released under version 2.0 of the [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

