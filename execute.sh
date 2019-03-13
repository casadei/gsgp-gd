#!/bin/bash

datasets=("airfoil" "ccn" "concrete" "keijzer-7" "parkinsons" "towerData" "vladislavleva-1" "vladislavleva-4" "yacht")
gsgp_path=$(pwd)"/dist"
experiments_path=$(pwd)"/experiments/scripts/gsgp"
results_path=$(pwd)"/results"
scripts_path=$(pwd)"/scripts"

mkdir -p "$results_path"

for dataset in "${datasets[@]}"
do
    echo "Executing $dataset"
    java -Xms1g -Xmx8g -jar "$gsgp_path"/gsgp-mo.jar -p "$experiments_path"/"$dataset".param #> "$results_path"/"$dataset".out
done
