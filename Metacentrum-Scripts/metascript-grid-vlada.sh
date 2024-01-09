#!/bin/bash

for ss in 1 2 3 4 5 6 7 8 9 10 20
do
  for tf in 0.20 0.40 0.60 0.80 1.00 1.20 1.40 1.60 1.80 2.00
  do
    echo $ss $tf
    qsub -v searchSteps=$ss,timeToFinish=$tf script-grid-vlada.sh
  done
done