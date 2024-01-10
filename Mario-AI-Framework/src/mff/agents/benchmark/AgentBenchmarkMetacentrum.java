package mff.agents.benchmark;

import engine.core.MarioAgent;
import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;
// import mff.agents.astarGrid.AStarTree;
import mff.agents.astarWindow.AStarTree;
import mff.agents.common.IGridHeuristic;
import mff.agents.common.IMarioAgentMFF;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import static mff.agents.common.AStarGridHelper.giveLevelTilesWithPath;
import static mff.LevelLoader.getLevel;

public class AgentBenchmarkMetacentrum {

    private static final DecimalFormat twoFractionDigitsDotSeparator;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.');
        twoFractionDigitsDotSeparator = new DecimalFormat("0.00", symbols);
    }

    private static final ArrayList<String> agents = new ArrayList<>() {{
//        add("robinBaumgarten");
//        add("robinBaumgartenSlimWindowAdvance");
//        add("astar");
//        add("astarPlanningDynamic");
       add("astarWindow");
        // add("astarGrid");
    }};

    private static final ArrayList<String> levels = new ArrayList<>() {{
        add("original");
        add("krys");
        // add("ge");
        // add("hopper");
        // add("notch");
        // add("notchParam");
        // add("notchParamRand");
        // add("ore");
        add("patternCount");
        // add("patternOccur");
        // add("patternWeightCount");
    }};

    public static void main(String[] args) throws IOException {
        
        try {
            AStarTree.SEARCH_STEPS = Integer.parseInt(args[0]);
            AStarTree.RIGHT_WINDOW_BORDER_X = Integer.parseInt(args[1]);
            // AStarTree.TIME_TO_FINISH_WEIGHT = Float.parseFloat(args[1]);

            // AStarTree.NODE_DEPTH_WEIGHT = Float.parseFloat(args[0]);
            // AStarTree.TIME_TO_FINISH_WEIGHT = Float.parseFloat(args[1]);
            // AStarTree.DISTANCE_FROM_PATH_TOLERANCE = Float.parseFloat(args[2]);
            // AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = Float.parseFloat(args[3]);
        } catch (Exception e) {
            System.out.println("Meta parameters not set successfully.");
            throw e;
        }

        for (var agentType : agents) {
            for (String level : levels) {
                File log = prepareLog("agent-benchmark" + File.separator + agentType + "-" + level
                        + "-SS-" + AStarTree.SEARCH_STEPS
                        + "-RWB-" + AStarTree.RIGHT_WINDOW_BORDER_X                        
                        + ".csv");

                if (log == null)
                    return;
                FileWriter logWriter = new FileWriter(log);

                logWriter.write("SS:" + AStarTree.SEARCH_STEPS + "\n");
                logWriter.write("RWB:" + AStarTree.RIGHT_WINDOW_BORDER_X + "\n");
                logWriter.write("level,win/fail,% travelled,run time,game ticks,planning time,total plannings,nodes evaluated,most backtracked nodes\n");

                warmup(agentType);

                if (level.equals("original"))
                    testOriginalLevels(agentType, logWriter);
                else if (level.equals("krys"))
                    testKrysLevels(agentType, logWriter);
                else
                    testFrameworkLevels(agentType, logWriter, level);

                logWriter.close();
            }
        }
        
    }

    private static void testFrameworkLevels(String agentType, FileWriter log, String levelsName) throws IOException {
        AgentStats agentStats;
        if (!agentType.equals("robinBaumgarten")) {
            for (int i = 1; i <= 16; i++) {
                System.out.println(agentType + "-" + levelsName + "-" + i);
                String level = getLevel("./levels/" + levelsName + "/lvl-" + i + ".txt");
                AgentBenchmarkGame game = new AgentBenchmarkGame();
                IMarioAgentMFF agent = getNewAgent(agentType);
                if (agent instanceof IGridHeuristic)
                    giveLevelTilesWithPath(agent, "./levels/" + levelsName + "/lvl-" + i + ".txt");
                // only 30 seconds to speed-up timeout if agent is stuck
                agentStats = game.runGame(agent, level, 30, 0, false);
                agentStats.level = levelsName + "-" + i;
                printStats(log, agentStats, ((IAgentBenchmarkBacktrack) agent).getMostBacktrackedNodes());
            }
        }
        else {
            for (int i = 1; i <= 16; i++) {
                System.out.println(agentType + "-" + levelsName + "-" + i);
                String level = getLevel("./levels/" + levelsName + "/lvl-" + i + ".txt");
                OriginalAgentBenchmarkGame game = new OriginalAgentBenchmarkGame();
                MarioAgent agent = new agents.robinBaumgarten.Agent();
                // only 30 seconds to speed-up timeout if agent is stuck
                agentStats = game.runGame(agent, level, 30, 0, false);
                agentStats.level = levelsName + "-" + i;
                printStats(log, agentStats, 0);
            }
        }
    }

    private static void testKrysLevels(String agentType, FileWriter log) throws IOException {
        AgentStats agentStats;
        if (!agentType.equals("robinBaumgarten")) {
            for (int i = 1; i <= 16; i++) {
                System.out.println(agentType + "-" + "krys" + "-" + i);
                String level = getLevel("./levels/krys/lvl-" + i + ".txt");
                AgentBenchmarkGame game = new AgentBenchmarkGame();
                IMarioAgentMFF agent = getNewAgent(agentType);
                if (agent instanceof IGridHeuristic)
                    giveLevelTilesWithPath(agent, "./levels/krys/lvl-" + i + ".txt");
                // only 30 seconds to speed-up timeout if agent is stuck
                agentStats = game.runGame(agent, level, 30, 0, false);
                agentStats.level = "Krys-" + i;
                printStats(log, agentStats, ((IAgentBenchmarkBacktrack) agent).getMostBacktrackedNodes());
            }
        }
        else {
            for (int i = 1; i <= 16; i++) {
                System.out.println(agentType + "-" + "krys" + "-" + i);
                MarioLevelGenerator generator = new levelGenerators.krys.LevelGenerator(i);
                String level = generator.getGeneratedLevel(new MarioLevelModel(150, 16),
                        new MarioTimer(5 * 60 * 60 * 1000));
                OriginalAgentBenchmarkGame game = new OriginalAgentBenchmarkGame();
                MarioAgent agent = new agents.robinBaumgarten.Agent();
                // only 30 seconds to speed-up timeout if agent is stuck
                agentStats = game.runGame(agent, level, 30, 0, false);
                agentStats.level = "Krys-" + i;
                printStats(log, agentStats, 0);
            }
        }
    }

    private static void testOriginalLevels(String agentType, FileWriter log) throws IOException {
        AgentStats agentStats;
        if (!agentType.equals("robinBaumgarten")) {
            for (int i = 1; i < 16; i++) {
                System.out.println(agentType + "-" + "original" + "-" + i);
                AgentBenchmarkGame game = new AgentBenchmarkGame();
                IMarioAgentMFF agent = getNewAgent(agentType);
                if (agent instanceof IGridHeuristic)
                    giveLevelTilesWithPath(agent, "./levels/original/lvl-" + i + ".txt");
                String level = getLevel("./levels/original/lvl-" + i + ".txt");
                agentStats = game.runGame(agent, level,200, 0, false);
                agentStats.level = "Mario-" + i;
                printStats(log, agentStats, ((IAgentBenchmarkBacktrack) agent).getMostBacktrackedNodes());
            }
        }
        else {
            for (int i = 1; i < 16; i++) {
                System.out.println(agentType + "-" + "original" + "-" + i);
                OriginalAgentBenchmarkGame game = new OriginalAgentBenchmarkGame();
                MarioAgent agent = new agents.robinBaumgarten.Agent();
                String level = getLevel("./levels/original/lvl-" + i + ".txt");
                agentStats = game.runGame(agent, level, 200, 0, false);
                agentStats.level = "Mario-" + i;
                printStats(log, agentStats, 0);
            }
        }
    }

    private static void warmup(String agentType) {
        System.out.println("WARMUP: " + agentType + "-" + "original-1");
        if (!agentType.equals("robinBaumgarten")) {
            AgentBenchmarkGame game = new AgentBenchmarkGame();
            IMarioAgentMFF agent = getNewAgent(agentType);
            if (agent instanceof IGridHeuristic)
                giveLevelTilesWithPath(agent, "./levels/original/lvl-1.txt");
            String level = getLevel("./levels/original/lvl-1.txt");
            game.runGame(agent, level,200, 0, false);
        }
        else {
            OriginalAgentBenchmarkGame game = new OriginalAgentBenchmarkGame();
            MarioAgent agent = new agents.robinBaumgarten.Agent();
            String level = getLevel("./levels/original/lvl-1.txt");
            game.runGame(agent, level, 200, 0, false);
        }
    }

    private static void printStats(FileWriter writer, AgentStats stats, int mostBacktrackedNodes) throws IOException {
        writer.write(stats.level + ','
                + stats.win + ','
                + twoFractionDigitsDotSeparator.format(stats.percentageTravelled) + ','
                + stats.runTime + ','
                + stats.totalGameTicks + ','
                + stats.totalPlanningTime + ','
                + stats.searchCalls + ','
                + stats.nodesEvaluated + ','
                + mostBacktrackedNodes + '\n'
        );
    }

    private static File prepareLog(String name) throws IOException {
        File agentBenchmarkFolder = new File("agent-benchmark");
        if (!agentBenchmarkFolder.exists()) {
            if (!agentBenchmarkFolder.mkdir()) {
                System.out.println("Can't create folder: " + agentBenchmarkFolder.getName());
                return null;
            }
        }

        File log = new File(name);
        if (log.exists()) {
            if (!log.delete()) {
                System.out.println("Can't delete file: " + log.getName());
                return null;
            }
        }
        if (!log.createNewFile()) {
            System.out.println("Can't create file: " + log.getName());
            return null;
        }
        return log;
    }

    private static IMarioAgentMFF getNewAgent(String agentType) {
        switch (agentType) {
            case "astar":
                return new mff.agents.astar.Agent();
            case "astarDistanceMetric":
                return new mff.agents.astarDistanceMetric.Agent();
            case "astarPlanningDynamic":
                return new mff.agents.astarPlanningDynamic.Agent();
            case "astarWindow":
                return new mff.agents.astarWindow.Agent();
            case "robinBaumgartenSlim":
                return new mff.agents.robinBaumgartenSlim.Agent();
            case "robinBaumgartenSlimImproved":
                return new mff.agents.robinBaumgartenSlimImproved.Agent();
            case "robinBaumgartenSlimWindowAdvance":
                return new mff.agents.robinBaumgartenSlimWindowAdvance.Agent();
            case "astarGrid":
                return new mff.agents.astarGrid.Agent();
            default:
                throw new IllegalArgumentException("Agent not supported.");
        }
    }
}
