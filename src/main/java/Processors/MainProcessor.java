package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;

import java.util.Comparator;
import java.util.stream.Collectors;

public class MainProcessor extends Processor {
    private ActionWeight maxWeight = new ActionWeight(0, Double.NEGATIVE_INFINITY);
    private PlayerActions bestAction = PlayerActions.STOP;

    public MainProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    public void process() {
    }

    public void process(PlayerAction playerAction) {
        // Process bisa dianalogikan sebagai sort action didalam algoritma greedy
        FoodProcessor foodProcessor = new FoodProcessor(bot, gameState);
        foodProcessor.process();
        processOneProcessor(foodProcessor);

        ObstacleProcessor obstacleProcessor = new ObstacleProcessor(bot, gameState);
        obstacleProcessor.process();
        processOneProcessor(obstacleProcessor);

        playerAction.action = bestAction;
        playerAction.heading = maxWeight.getHeading();
    }

    private void processOneProcessor(Processor p) {
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
}
