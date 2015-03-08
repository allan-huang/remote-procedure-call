## Remote Function Call ##

**Remote Function Call** is a Netty 4-based Remote Procedure Call (RPC) system.

This is my first GitHub project for open source. I has rewritten the source codes and completes the new practical features.

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

- [Netty 4.2.6](http://netty.io/index.html) or later stable version except Netty 5.0.
- [Google Gson 2.3.1](https://code.google.com/p/google-gson/) or later stable version.
- [Apache Commons Lang 3.3.2](http://commons.apache.org/proper/commons-lang/index.html) or later stable version.
- [SLF4j 1.7.10](http://www.slf4j.org/) or later stable version.
- [Log4j 2.2](http://logging.apache.org/log4j/2.x/) or later stable version.
- [JZlib 1.1.3](http://www.jcraft.com/jzlib/) or later stable version.
- [Hamcrest 1.3](https://code.google.com/p/hamcrest/) or later stable version.
- [JUnit 4.1.2](http://junit.org/) or later stable version.

### Get Started ###
Please refer to three classes defined in the `test.tw.me.ychuang.rfc` package: `BizServiceSkeleton`, `BizServiceStub`, and `BizServiceTest` classes separately.  

### License ###

**Remote Function Call** is released under version 2.0 of the [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

