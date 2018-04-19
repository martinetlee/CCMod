#!/bin/bash

# ssh machineName 'bash -s' < script.sh

Machines=("green01" "green02" "green03" "green04" "green05" "green06" "green07")

for machine in "${Machines[@]}"
do
    echo $machine 
    ssh $machine 'pkill -f cc.test.example3.Server' & 
done

