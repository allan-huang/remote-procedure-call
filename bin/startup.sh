#!/bin/bash

MODE=$1
JAVA_HOME=/usr/java/latest
RPC_HOME=/usr/local/rpc

LIB_PATH=`dirname $0`

CLASSES=$LIB_PATH
CLASSES=$CLASSES:$LIB_PATH/rpc-client.properties
CLASSES=$CLASSES:$LIB_PATH/rpc-server.properties
#CLASSES=$CLASSES:$LIB_PATH/rpc-simulator.properties

CLASSES=$CLASSES:$LIB_PATH/lib/commons-lang3-3.3.2.jar
CLASSES=$CLASSES:$LIB_PATH/lib/gson-2.3.1.jar
CLASSES=$CLASSES:$LIB_PATH/lib/jzlib-1.1.3.jar
CLASSES=$CLASSES:$LIB_PATH/lib/log4j-api-2.2.jar
CLASSES=$CLASSES:$LIB_PATH/lib/log4j-core-2.2.jar
CLASSES=$CLASSES:$LIB_PATH/lib/log4j-jcl-2.2.jar
CLASSES=$CLASSES:$LIB_PATH/lib/log4j-slf4j-impl-2.2.jar
CLASSES=$CLASSES:$LIB_PATH/lib/netty-all-4.0.26.Final.jar
CLASSES=$CLASSES:$LIB_PATH/lib/slf4j-api-1.7.10.jar
CLASSES=$CLASSES:$LIB_PATH/lib/ychuang-rpc-1.0.1.jar
#CLASSES=$CLASSES:$LIB_PATH/lib/test-ychuang-rpc-1.0.1.jar

if [ "$MODE" == "" ] || [ "$MODE" == "b" ] ; then
	echo "Starting up YCHuang RPC in background."	
	nohup $JAVA_HOME/bin/java -Xms1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m -cp $CLASSES tw.me.ychuang.rpc.Main > $RPC_HOME/logs/rpc.out 2>&1 &	
	#nohup $JAVA_HOME/bin/java -Xms1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m -cp $CLASSES test.tw.me.ychuang.rpc.Simulator > $RPC_HOME/logs/rpc.out 2>&1 &	
else [ "$MODE" == "f" ]
	echo "Starting up YCHuang RPC in foreground."	
	$JAVA_HOME/bin/java -Xms1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m -cp $CLASSES tw.me.ychuang.rpc.Main
	#$JAVA_HOME/bin/java -Xms1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m -cp $CLASSES test.tw.me.ychuang.rpc.Simulator
fi

exit 0
