echo OFF

set LIB_PATH=
set LIB_PATH=%LIB_PATH%;.\log4j2.xml
set LIB_PATH=%LIB_PATH%;.\rpc-client.properties
set LIB_PATH=%LIB_PATH%;.\rpc-server.properties
set LIB_PATH=%LIB_PATH%;.\lib\*

set MAIN_CLASS=tw.me.ychuang.rpc.Main

set JAVA_OPTS=-Xmx1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m

echo ON

%JAVA_HOME%\bin\java %JAVA_OPTS% -cp %LIB_PATH% %MAIN_CLASS%
