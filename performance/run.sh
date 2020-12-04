#!/bin/bash

current_time=$(date "+%Y.%m.%d-%H.%M.%S")
filename="results_${current_time}.dat"

./jmeter/bin/jmeter.sh -t performance.jmx -n  -l "${filename}"
