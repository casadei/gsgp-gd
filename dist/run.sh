#!/bin/bash
java -jar -Xmx7g gsgp-gd.jar -p ~/experiments/scripts/gsgp/airfoil.param 2>&1 > /tmp/airfoil.out
java -jar -Xmx7g gsgp-gd.jar -p ~/experiments/scripts/gsgp/concrete.param 2>&1 > /tmp/concrete.out
java -jar -Xmx7g gsgp-gd.jar -p ~/experiments/scripts/gsgp/keijzer-5.param 2>&1 > /tmp/keijzer-5.out
java -jar -Xmx7g gsgp-gd.jar -p ~/experiments/scripts/gsgp/keijzer-7.param 2>&1 > /tmp/keijzer-7.out
java -jar -Xmx7g gsgp-gd.jar -p ~/experiments/scripts/gsgp/vladislavleva-1.param 2>&1 > /tmp/vladislavleva-1.out
java -jar -Xmx7g gsgp-gd.jar -p ~/experiments/scripts/gsgp/yacht.param 2>&1 > /tmp/yacht.out
