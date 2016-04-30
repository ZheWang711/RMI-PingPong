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
