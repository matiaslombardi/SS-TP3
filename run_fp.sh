#!/bin/bash

N=(100)
SLIT_SIZES=(0.01 0.03 0.05)

rm graphics/fps.txt

for n in ${N[@]}; do
    for slit_size in ${SLIT_SIZES[@]}; do
        printf "${n} ${slit_size}\n" >> graphics/fps.txt
        /usr/bin/env /Users/srosati/Library/Java/JavaVirtualMachines/openjdk-18.0.2.1/Contents/Home/bin/java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp /Users/srosati/Library/Application\ Support/Code/User/workspaceStorage/0cbe8661bfbfecf259f025c067e12e3b/redhat.java/jdt_ws/SS-TP3_3fa4554a/bin main.java.ar.edu.itba.ss.EventBasedSystem ${n} ${slit_size} 0.001
        cd graphics
        python3 fp.py
        cd ..
    done
done

cd graphics
python3 graph_fp.py
