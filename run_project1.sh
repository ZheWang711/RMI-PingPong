#!/usr/bin/env bash

clear
echo "----------Running Skeleton/Stub Tests----------"
sleep 3
cd project1
make
echo "----------Running Basic Tests----------"
java conformance.ConformanceTests
sleep 2
echo "----------Running Extensive Tests----------"
java conformance2.ConformanceTests
sleep 2
echo "----------Running Unit Tests----------"
java -cp ./:./unit unit.UnitTests
sleep 2
echo "----------Running Self Tests----------"
java test.SelfTest
make clean
sleep 2
cd ..


echo "----------Running Docker Test----------"
sleep 3
chmod +x project1_Docker/run.sh project1_Docker/clean.sh
cd project1_Docker
./run.sh
cd ..
echo "----------Test Completed----------"
