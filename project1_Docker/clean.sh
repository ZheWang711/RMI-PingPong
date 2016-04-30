#!/usr/bin/env bash
echo "Start cleaning"
docker rm -f pingserver pingclient dbvc1
docker network rm lab1test
docker rmi -f zhe/lab1dv zhe/lab1cs
docker-machine rm -y ZheHuiVM
rm -f client_log.txt server_log.txt
echo "Finished cleaning"
