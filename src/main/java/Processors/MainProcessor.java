package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;

import java.util.Comparator;
import java.util.stream.Collectors;

public class MainProcessor extends Processor {

    private ActionWeight maxWeight = new ActionWeight(0, Double.NEGATIVE_INFINITY);
    private PlayerActions bestAction = PlayerActions.Stop;

    public MainProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    public void process() {
        FoodProcessor foodProcessor = new FoodProcessor(bot, gameState);
        processOneProcessor(foodProcessor);

        ObstacleProcessor obstacleProcessor = new ObstacleProcessor(bot, gameState);
        processOneProcessor(obstacleProcessor);

        EdgeProcessor edgeProcessor = new EdgeProcessor(bot, gameState);
        processOneProcessor(edgeProcessor);

        TorpedoProcessor torpedoProcessor = new TorpedoProcessor(bot, gameState);
        processOneProcessor(torpedoProcessor);
    }

    private void processOneProcessor(Processor p) {
        p.process();
        for (var key : p.data.keySet()) {
            var value = p.data.get(key);
            var res = value.stream().sorted(Comparator.comparing(ActionWeight::getWeight)).collect(Collectors.toList());
            var maxWeight = res.get(res.size() - 1);
            if (this.maxWeight.getWeight() < maxWeight.getWeight()) {
                this.maxWeight = maxWeight;
                this.bestAction = key;
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
