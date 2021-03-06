#!/bin/bash

MODE=$1
JAVA_HOME=/usr/java/latest
RPC_HOME=/usr/local/rpc
MAIN_CLASS='tw.me.ychuang.rpc.Main'

LIB_PATH=`dirname $0`

CLASSES=$LIB_PATH
CLASSES=$CLASSES:$LIB_PATH/log4j2.xml
CLASSES=$CLASSES:$LIB_PATH/rpc-client.properties
CLASSES=$CLASSES:$LIB_PATH/rpc-server.properties
CLASSES=$CLASSES:$LIB_PATH/lib/*

if [ "$MODE" == "" ] || [ "$MODE" == "b" ] ; then
	echo "Starting up YCHuang RPC in background."	
	nohup $JAVA_HOME/bin/java -Xmx1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m -cp $CLASSES $MAIN_CLASS > $RPC_HOME/logs/rpc.out 2>&1 &	
else [ "$MODE" == "f" ]
	echo "Starting up YCHuang RPC in foreground."	
	$JAVA_HOME/bin/java -Xmx1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m -cp $CLASSES $MAIN_CLASS
fi

exit 0
