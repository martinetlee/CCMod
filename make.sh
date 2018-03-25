#!/bin/bash

#sigar=/home/$USER/packages/sigar/bindings/java/sigar-bin/lib
base=/home/$USER/CCTest

SRC=$base/src
LIB=$base/lib
BIN=$base/bin

x=`find ${SRC} -name "*.java"`
rm -r ${BIN}/cc/test
#cp ${LIB}/*.so ${BIN}
#cp ${LIB}/*.jar ${BIN}

INCLUDELIB=''
for entry in "${LIB}"/*
do
  INCLUDELIB="${INCLUDELIB}:$entry"
done
echo "###############"
echo "${INCLUDELIB}"
echo "###############"
#echo "$x"

#javac -g -cp .:${BIN}:${LIB}${INCLUDELIB} -d ${BIN} /home/s469lee/CCTest/src/cc/test/commons/Get.java
javac -g -cp .:${BIN}:${LIB}${INCLUDELIB} -d ${BIN} $x

# javac -g -cp .:${BIN}:${LIB}:${LIB}/jverbs.jar:${LIB}/commons-math3-3.6.jar:${LIB}/chronicle-map-3-8-0.jar:${LIB}/chronicle.jar:${LIB}/sigar.jar:${LIB}/libthrift-1.0.0.jar:${LIB}/simple-5.1.1.jar:${LIB}/slf4j-api-1.6.4.jar -d ${BIN} $x
