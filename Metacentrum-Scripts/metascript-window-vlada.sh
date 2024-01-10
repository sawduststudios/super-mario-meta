#!/bin/bash

for ss in 1 2 3 4 5 6 7 8 9 10 20
do
  for rbw in 48 96 124 150 176 200 224 248 272 296 320
  do
    echo $ss $rbw
    qsub -v searchSteps=$ss,rightBorderWindow=$rbw script-grid-vlada.sh
  done
done