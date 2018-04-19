#!/bin/bash

# ssh machineName 'bash -s' < script.sh

binDir="~/CCMod/bin"

Machines=("green07" "green02" "green03" "green04" "green05" "green06" "green01")

rm -rf testLog/*
rm -rf logs/*


for machine in "${Machines[@]}"
do
    echo "Stop process and cleaning logs on $machine" 
    ssh $machine 'pkill -f cc.test.example3.Server' &
    ssh $machine "rm -rf /tmp/logs/*" & 
done



