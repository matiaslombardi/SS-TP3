#!/bin/bash

N=100
SLIT_SIZE=0.02
V=(0.01 0.02 0.03)

rm graphics/pvt.txt

for v in ${V[@]}; do
    printf "${v}\n" >> graphics/pvt.txt
    /usr/bin/env /Users/srosati/Library/Java/JavaVirtualMachines/openjdk-18.0.2.1/Contents/Home/bin/java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp /Users/srosati/Library/Application\ Support/Code/User/workspaceStorage/0cbe8661bfbfecf259f025c067e12e3b/redhat.java/jdt_ws/SS-TP3_3fa4554a/bin main.java.ar.edu.itba.ss.EventBasedSystem ${N} ${SLIT_SIZE} ${v} 0.001
    cd graphics
    python3 pvt.py
    cd ..
done

cd graphics
python3 graph_pvt.py
