#!/usr/bin/env bash

clear
sleep 1
echo "----------starting testing----------"

docker-machine create --driver virtualbox ZheHuiVM
eval $(docker-machine env ZheHuiVM)

clear
sleep 1
echo "----------building data volume image----------"
docker build -t zhe/lab1dv ./DockerFiles/DataVolume/


echo "----------building client & server image----------"
docker build -t zhe/lab1cs ./DockerFiles/ClientServer/

clear
sleep 1
echo "----------creating network----------"
docker network create lab1test

echo "----------creating data volume container----------"
docker create -v /data --name dbvc1 zhe/lab1dv /bin/true

echo "----------run client &server containers----------"
docker run --volumes-from dbvc1 -d --name=pingserver --net=lab1test zhe/lab1cs /data/runServer.sh
sleep 3
docker run --volumes-from dbvc1 -d --name=pingclient --net=lab1test zhe/lab1cs /data/runClient.sh

echo "----------client logs will be displayed in 5 seconds----------"
sleep 5
docker logs pingclient
docker logs pingclient > client_log.txt

echo "----------server logs will be displayed in 2 seconds----------"
sleep 2
docker logs pingserver
docker logs pingserver > server_log.txt

# todo: compute how many correct "pong" using python or shell
sleep 2
echo ""
echo ""
echo "----------judging results----------"

suc=0
fail=0

for i in `seq 0 3`
do
    if grep -Fxq "Pong $i" client_log.txt
    then
        echo "Found Pong $i"
        let "suc += 1"
    else
        echo "No Pong $i was found"
    fi
done
let "fail = 4-suc"

echo ""

echo "$suc Tests Completed, $fail Tests Failed"


sleep 3
echo "----------cleaning files----------"
./clean.sh












