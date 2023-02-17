package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.MathService;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TorpedoProcessorObstacle extends Processor {
    final static double MINVAL = 20.0;
    final static double thresholdDistance = 5.0;

    public TorpedoProcessorObstacle(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getProjectedPosition();
        var gasCloudList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD && MathService.getDistanceBetween(bot, item) < 20)
//                .sorted(Comparator
//                        .comparing(item -> MathService.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        var ActionHeadingList = new ArrayList<ActionWeight>();
        PlayerActions actionTaken = PlayerActions.FireTorpedoes;
        double worldRadius = gameState.getWorld().radius;


        // Only trigger if the player has more than some number MINVAL
        for (GameObject obj : gasCloudList) {
            if (bot.getSize() > MINVAL && bot.torpedoSalvoCount > 0) {
                int heading = MathService.getHeadingBetween(bot, obj);
                double weight = 0;

                boolean biggerThanHalf = obj.getSize() > bot.getSize() * 0.5;
                // v = d/t
                // v : bot.speed    t -> per tick -1 size -> t <= 0.5 * bot.size (keep half of the size after traversing the cloud)
                // t = d/v <= 0.5 * bot.size
                boolean canHead = obj.getSize() * 2 / bot.getSpeed() <= 0.5 * bot.getSize();
                boolean canDestroy = !biggerThanHalf && obj.getSize() < 15;

                var worldCenter = new Position(0, 0);
                double distanceToCenter = MathService.getDistanceBetween(botPos, worldCenter);

                if (worldRadius - distanceToCenter > thresholdDistance && heading == MathService.getHeadingBetween(bot.getPosition(), worldCenter)) {
                    if (canHead) {
                        actionTaken = PlayerActions.Forward;
                        weight = 601;
                        heading = MathService.getHeadingBetween(bot.getPosition(), worldCenter);
                    } else if (canDestroy) {
                        weight = 601;
                    }
                } else {
                    weight = 2;
                }

                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
                this.data.put(actionTaken, ActionHeadingList);
            }

        }
    }
}
