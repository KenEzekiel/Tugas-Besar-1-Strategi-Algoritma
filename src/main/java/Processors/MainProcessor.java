package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainProcessor extends Processor {

    private ActionWeight maxWeight = new ActionWeight(0, Double.NEGATIVE_INFINITY);
    private PlayerActions bestAction = PlayerActions.Stop;

    public MainProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    public void process() {
        FoodProcessor foodProcessor = new FoodProcessor(bot, gameState);
        ObstacleProcessor obstacleProcessor = new ObstacleProcessor(bot, gameState);
        EdgeProcessor edgeProcessor = new EdgeProcessor(bot, gameState);
        TorpedoProcessorPlayer torpedoProcessorPlayer = new TorpedoProcessorPlayer(bot, gameState);
        TeleportProcessor teleportProcessor = new TeleportProcessor(bot, gameState);
        ShieldProcessor shieldProcessor = new ShieldProcessor(bot, gameState);
        EnemyProcessor enemyProcessor = new EnemyProcessor(bot, gameState);
//        LateGameProcessor lateGameProcessor = new LateGameProcessor(bot, gameState);
        RunFromTeleporterProcessor runFromTeleporterProcessor = new RunFromTeleporterProcessor(bot, gameState);

        ArrayList<Processor> processors = new ArrayList<>(List.of(new Processor[]{
                foodProcessor,
                obstacleProcessor,
                edgeProcessor,
                torpedoProcessorPlayer,
                teleportProcessor,
                shieldProcessor,
                enemyProcessor,
                runFromTeleporterProcessor
//                lateGameProcessor
        }));


//        threading
        for (Processor p : processors) {
            p.start();
        }

        boolean allProcessed = false;
        while (!allProcessed) {
            allProcessed = true;
            for (Processor p : processors) {
                if (!p.processed) {
                    if (p.isAlive()) allProcessed = false;
                    else {
                        processOneProcessor(p);
                        p.processed = true;
                    }
                }
            }
        }


    }

    private void processOneProcessor(Processor p) {
        for (var key : p.data.keySet()) {
            var value = p.data.get(key);
            var res = value.stream().max(Comparator.comparing(ActionWeight::getWeight));
            if (res.isPresent()) {
                var maxWeight = res.get();
                if (this.maxWeight.getWeight() < maxWeight.getWeight()) {
                    this.maxWeight = maxWeight;
                    this.bestAction = key;
                }
            }
        }

    }

    public PlayerActions getBestAction() {
        return bestAction;
    }

    public ActionWeight getMaxWeight() {
        return maxWeight;
    }
}
