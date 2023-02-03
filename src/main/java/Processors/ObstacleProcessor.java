package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class ObstacleProcessor extends Processor {
    public ObstacleProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {

        if (!gameState.getGameObjects().isEmpty()) {
            var obstacleList = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.GAS_CLOUD || item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD))
                    .sorted(Comparator
                            .comparing(item -> MathService.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
            var ActionHeadingList = new ArrayList<ActionWeight>();

            for (GameObject obj : obstacleList) {
                int heading = MathService.reverseHeading(MathService.getHeadingBetween(bot, obj));
                // weight akan berpengaruh pada prioritas algoritma untuk mengambil action ini
                double weight = 5 / MathService.getDistanceBetween(bot, obj);
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }

            this.data.put(PlayerActions.FORWARD, ActionHeadingList);
        }
    }
}
