#!/bin/bash

N=(100)
SLIT_SIZES=(0.01 0.02 0.03)

rm graphics/times.txt

for n in ${N[@]}; do
    for slit_size in ${SLIT_SIZES[@]}; do
        for i in {1..10}; do
            printf "${n} ${slit_size}\n" >> graphics/times.txt
            /usr/bin/env /Users/srosati/Library/Java/JavaVirtualMachines/openjdk-18.0.2.1/Contents/Home/bin/java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp /Users/srosati/Library/Application\ Support/Code/User/workspaceStorage/0cbe8661bfbfecf259f025c067e12e3b/redhat.java/jdt_ws/SS-TP3_3fa4554a/bin main.java.ar.edu.itba.ss.EventBasedSystem ${n} ${slit_size} 0.1 0.001
            cd graphics
            python3 time.py
            cd ..
        done
    done
done

cd graphics
python3 graph_time.py
