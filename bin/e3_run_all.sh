#!/bin/bash

# ssh machineName 'bash -s' < script.sh

binDir="~/CCMod/bin"

Machines=("green07" "green02" "green03" "green04" "green05" "green06" "green01")

rm -rf testLog/*
rm -rf logs/*
rm -rf /tmp/logs/*


for machine in "${Machines[@]}"
do
    echo $machine 
    ssh $machine "cd $binDir;./e3_run_server.sh &> testLog/$machine.txt" &
done



