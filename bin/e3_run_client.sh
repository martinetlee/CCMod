#!/bin/bash

#sigar=/home/$USER/packages/sigar/bindings/java/sigar-bin/lib

getIP(){
if [ "$1" == "green01" ]; then
        ip="10.0.2.1"
elif [ "$1" == "green02" ]; then
        ip="10.0.2.2"
elif [ "$1" == "green03" ]; then
        ip="10.0.2.3"
elif [ "$1" == "green04" ]; then
        ip="10.0.2.4"
elif [ "$1" == "green05" ]; then
        ip="10.0.2.5"
elif [ "$1" == "green06" ]; then
        ip="10.0.2.6"
elif [ "$1" == "green07" ]; then
        ip="10.0.2.7"
elif [ "$1" == "green08" ]; then
        ip="10.0.2.8"
elif [ "$1" == "green09" ]; then
        ip="10.0.2.9"
elif [ "$1" == "green10" ]; then
        ip="10.0.2.10"
elif [ "$1" == "green11"  ]; then
        ip="10.0.2.11"
elif [ "$1" == "green12"  ]; then
        ip="10.0.2.12"
elif [ "$1" == "green13"  ]; then
        ip="10.0.2.13"
elif [ "$1" == "green14"  ]; then
        ip="10.0.2.14"
elif [ "$1" == "green15"  ]; then
        ip="10.0.2.15"
fi
}

base=/home/$USER/CCTest

SRC=$base/src
LIB=$base/lib
BIN=$base/bin

INCLUDELIB=''
for entry in "${LIB}"/*
do
  INCLUDELIB="${INCLUDELIB}:$entry"
done

HOSTNAME="$(hostname)"

echo $HOSTNAME

getIP $HOSTNAME
HOSTNAME="$HOSTNAME-client"
port=5987

#(1) ./Server identifier server_ip server_port --> main server that bootstraps the cluster
#(2) ./Server identifier server_ip server_port cluster_ip cluster_port --> normal server

echo "java -cp .:${BIN}:${LIB}${INCLUDELIB} cc.test.example3.Client $HOSTNAME $ip $port"

java -cp .:${BIN}:${LIB}${INCLUDELIB} cc.test.example3.Client $HOSTNAME $ip $port


#java cc.test.example2.Server hi localhost 1234
#javac -g -cp .:${BIN}:${LIB}${INCLUDELIB} -d ${BIN} /home/s469lee/CCTest/src/cc/test/commons/Get.java
#javac -g -cp .:${BIN}:${LIB}${INCLUDELIB} -d ${BIN} $x

# javac -g -cp .:${BIN}:${LIB}:${LIB}/jverbs.jar:${LIB}/commons-math3-3.6.jar:${LIB}/chronicle-map-3-8-0.jar:${LIB}/chronicle.jar:${LIB}/sigar.jar:${LIB}/libthrift-1.0.0.jar:${LIB}/simple-5.1.1.jar:${LIB}/slf4j-api-1.6.4.jar -d ${BIN} $x
