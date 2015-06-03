ECHO OFF

SET LIB_PATH=
SET LIB_PATH=%LIB_PATH%;.\rpc-client.properties
SET LIB_PATH=%LIB_PATH%;.\rpc-server.properties
SET LIB_PATH=%LIB_PATH%;.\lib\*

SET JAVA_OPTS=-Xmx1024m -Xms1024m -XX:PermSize=64m -XX:MaxPermSize=64m

ECHO ON

%JAVA_HOME%\bin\java %JAVA_OPTS% -cp %LIB_PATH% tw.me.ychuang.rpc.Main
