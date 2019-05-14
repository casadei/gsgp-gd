#!/bin/bash

datasets=("bioavailability" "ccn" "ccun" "concrete" "energyCooling" "energyHeating" "keijzer-7" "parkinsons" "towerData" "vladislavleva-1" "wineRed" "wineWhite" "yacht")
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
