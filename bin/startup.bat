ECHO OFF

SET LIB_PATH=
SET LIB_PATH=%LIB_PATH%;.\rpc-client.properties
SET LIB_PATH=%LIB_PATH%;.\rpc-server.properties
rem SET LIB_PATH=%LIB_PATH%;.\rpc-simulator.properties

SET LIB_PATH=%LIB_PATH%;.\lib\commons-lang3-3.3.2.jar
SET LIB_PATH=%LIB_PATH%;.\lib\gson-2.3.1.jar
SET LIB_PATH=%LIB_PATH%;.\lib\jzlib-1.1.3.jar
SET LIB_PATH=%LIB_PATH%;.\lib\log4j-api-2.2.jar
SET LIB_PATH=%LIB_PATH%;.\lib\log4j-core-2.2.jar
SET LIB_PATH=%LIB_PATH%;.\lib\log4j-jcl-2.2.jar
SET LIB_PATH=%LIB_PATH%;.\lib\log4j-slf4j-impl-2.2.jar
SET LIB_PATH=%LIB_PATH%;.\lib\netty-all-4.0.26.Final.jar
SET LIB_PATH=%LIB_PATH%;.\lib\slf4j-api-1.7.10.jar
SET LIB_PATH=%LIB_PATH%;.\lib\ychuang-rpc-1.0.1.jar
rem SET LIB_PATH=%LIB_PATH%;.\lib\test-ychuang-rpc-1.0.1.jar

SET JAVA_OPTS=-Xmx1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m

ECHO ON

%JAVA_HOME%/bin/java %JAVA_OPTS% -cp %LIB_PATH% tw.me.ychuang.rpc.Main
rem %JAVA_HOME%/bin/java %JAVA_OPTS% -cp %LIB_PATH% test.tw.me.ychuang.rpc.Simulator
