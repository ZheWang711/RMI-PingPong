#!/usr/bin/env bash

docker run --volumes-from dbvc1 -it --name=pingclient --net=lab1test zhe/lab1cs /bin/bash
