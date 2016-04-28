#!/usr/bin/env bash
echo "----------starting testing----------"

echo "----------building data volume image----------"
docker build -t zhe/lab1dv ./DockerFiles/DataVolume/

echo "----------building client & server image----------"
docker build -t zhe/lab1cs ./DockerFiles/ClientServer/

echo "----------creating network----------"
docker network create lab1test

echo "----------creating data volume container----------"
docker create -v /data --name dbvc1 zhe/lab1dv /bin/true

echo "----------run client &server containers----------"
docker run --volumes-from dbvc1 -d --name=pingserver --net=lab1test zhe/lab1cs /data/runServer.sh
docker run --volumes-from dbvc1 -d --name=pingclient --net=lab1test zhe/lab1cs /data/runClient.sh

echo "----------client logs will be displayed in 2 seconds----------"
sleep 5
docker logs pingclient

echo "----------server logs will be displayed in 2 seconds----------"
sleep 2
docker logs pingserver

echo "----------cleaning files----------"
./clean.sh

# todo: compute how many correct "pong" using python or shell