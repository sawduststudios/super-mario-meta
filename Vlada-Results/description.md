# Description

## Common task

In the common task I have tested all combinations for theese different values for parameters searchSteps and timeToFinishWeight.

Search Steps in 1 2 3 4 5 6 7 8 9 10 20.
Time to finish weight in 0.20 0.40 0.60 0.80 1.00 1.20 1.40 1.60 1.80 2.00.

That is 110 combinations in total. Each configuration was run on the first 15 original levels, first 15 krys levels and first 15 patternCount levels.

I have tested it by accident on the astarGrid agent and not the simple astar agent, but the process is similar so atleast theese will be interesting to compare to what other students have found out about astar agent.

In the generated heatmaps we can see that the based on winrate the best parameter combinations are search steps value set to 2 or 3 and time to finish weight value between 1 and 2. Search steps 20 is bad, because the agent probably does not have enough time to do so many steps - because the average run time for 20 ss is quite low, compared with the low winrate this means that the agent probably died alot.

## Elective task

I chose to experiment with the windowAstar agent. I have tested all combinations for theese different values for parameters searchSteps and rightBorderWindowWidth:

Search Steps in 1 2 3 4 5 6 7 8 9 10 20.
Right border window width in 48 96 124 150 176 200 224 248 272 296 320.

That is 120 combinations in total. I have tried some smaller values than the default 176 but thought that it seems more interesting to make the window bigger. I have tested it on the first 15 original levels, first 100 krys levels, first 100 patternCount levels and first 100 notch levels.

In the generated heatmaps we can see that the higher the window the better the winrate. This is not very surprising, because the agent can see more of the level and therefore make better decisions. The search steps value does not seem to have a big impact on the winrate. The average run time is quite low for all combinations but lower values of search steps do seem to achieve lower run times, which again makes sense.

## Graphs

The graphs were generated using common-process.py and elective-process.py. The graphs show the average winrate and average run time for each combination of parameters. The graphs are saved in folders common-task-graphs and elective-task-graphs.
