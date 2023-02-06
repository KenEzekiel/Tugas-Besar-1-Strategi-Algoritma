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
    final static double VALUE = 5.0;

    public ObstacleProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        if (!gameState.getGameObjects().isEmpty()) {
            var botPos = bot.getProjectedPosition();
            var obstacleList = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.GAS_CLOUD || item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD))
                    .sorted(Comparator
                            .comparing(item -> MathService.getDistanceBetween(botPos, item.getPosition()) - bot.getSize() - item.getSize()))
                    .collect(Collectors.toList());
            var ActionHeadingList = new ArrayList<ActionWeight>();
            double worldDiameter = gameState.getWorld().radius * 2;
            for (GameObject obj : obstacleList) {
                int heading = MathService.reverseHeading(MathService.getHeadingBetween(botPos, obj.getPosition()));
                var distance = MathService.getDistanceBetween(botPos, obj.getPosition()) - bot.getSize() - obj.getSize();
                // weight akan berpengaruh pada prioritas algoritma untuk mengambil action ini
                double weight = VALUE * (worldDiameter - distance) / worldDiameter;
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }

            this.data.put(PlayerActions.Forward, ActionHeadingList);
        }
    }
}
