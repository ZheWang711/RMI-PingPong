#!/bin/bash
rm -rf /data/build
mkdir /data/build
javac -sourcepath /data/lab1 -d /data/build /data/lab1/**/*.java
java -cp /data/build PingPong.RunClient
