package mff.agents.astarGrid;

import mff.agents.astarHelper.MarioAction;
import mff.agents.benchmark.IAgentBenchmark;
import mff.agents.benchmark.IAgentBenchmarkBacktrack;
import mff.agents.common.IGridHeuristic;
import mff.agents.common.IMarioAgentMFF;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import java.util.ArrayList;

public class Agent implements IMarioAgentMFF, IAgentBenchmark, IGridHeuristic, IAgentBenchmarkBacktrack {

    private ArrayList<boolean[]> actionsList = new ArrayList<>();
    private float furthestDistance = -1;
    private boolean finished = false;
    private int totalSearchCalls = 0;
    private int totalNodesEvaluated = 0;
    private int mostBacktrackedNodes = 0;
    private int[][] levelTilesWithPath;

    @Override
    public void initialize(MarioForwardModelSlim model) {
        AStarTree.winFound = false;
        AStarTree.exitTileX = model.getWorld().level.exitTileX * 16;
    }

    @Override
    public void receiveLevelWithPath(int[][] levelTilesWithPath) {
        this.levelTilesWithPath = levelTilesWithPath;
    }

    @Override
    public boolean[] getActions(MarioForwardModelSlim model, MarioTimerSlim timer) {
        if (finished) {
            if (actionsList.size() == 0)
                return MarioAction.NO_ACTION.value;
            else
                return actionsList.remove(actionsList.size() - 1);
        }

        AStarTree tree = new AStarTree(model, 3, levelTilesWithPath);
        ArrayList<boolean[]> newActionsList = tree.search(timer);
        totalSearchCalls++;
        this.totalNodesEvaluated += tree.nodesEvaluated;
        this.mostBacktrackedNodes = Math.max(tree.mostBacktrackedNodes, this.mostBacktrackedNodes);

        if (AStarTree.winFound) {
            actionsList = newActionsList;
            finished = true;
            return actionsList.remove(actionsList.size() - 1);
        }

        if (tree.furthestNodeDistance > furthestDistance) {
            furthestDistance = tree.furthestNodeDistance;
            actionsList = newActionsList;
        }

        if (actionsList.size() == 0) { // didn't find a way further yet, take new actions to prevent stopping
            actionsList = newActionsList;
        }

        if (actionsList.size() == 0) // agent failed
            return MarioAction.NO_ACTION.value;

        return actionsList.remove(actionsList.size() - 1);
    }

    @Override
    public int getSearchCalls() {
        return totalSearchCalls;
    }

    @Override
    public int getNodesEvaluated() {
        return totalNodesEvaluated;
    }

    @Override
    public int getMostBacktrackedNodes() {
        return mostBacktrackedNodes;
    }

    @Override
    public String getAgentName() {
        return "MFF A* Grid Agent";
    }
}
