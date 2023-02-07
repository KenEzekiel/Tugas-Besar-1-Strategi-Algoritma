package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TorpedoProcessorObstacle extends Processor {
    final static double MINVAL = 16.0;
    final static double VALUE = 5.0;

    public TorpedoProcessorObstacle(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getProjectedPosition();
        var gasCloudList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
//                .sorted(Comparator
//                        .comparing(item -> MathService.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        var ActionHeadingList = new ArrayList<ActionWeight>();
        PlayerActions actionTaken = PlayerActions.Forward;
        double worldDiameter = gameState.getWorld().radius * 2;


        // Only trigger if the player has more than some number MINVAL
        // Weight will be + if player's salvo is nearing max
        for (GameObject obj : gasCloudList) {
            if (bot.getSize() > MINVAL && bot.torpedoSalvoCount > 0) {
                int heading = MathService.getHeadingBetween(bot, obj);
                var distance = MathService.getDistanceBetween(botPos, obj.getPosition()) - bot.getSize() - obj.getSize();
                if (distance > 100) {
                    continue;
                }
                double distanceValue = MathService.getDistanceBetween(bot, obj) != 0 ? (worldDiameter - distance) / worldDiameter : 5;
                // 1.2 and 0.3 is still an arbitrary value
//                double headingValue = bot.currentHeading == heading ? 1.2 : 0.3;
                double groupValue;

                // Can be changed, how near, how grouped, how big
                double weight;

                boolean biggerThanHalf = obj.getSize() > bot.getSize() * 0.5;
                if (biggerThanHalf || bot.getSize() <= MINVAL || bot.torpedoSalvoCount == 0) {
                    actionTaken = PlayerActions.Forward;
                    heading = MathService.reverseHeading(heading);
                    weight = distanceValue;
                } else {
                    actionTaken = PlayerActions.FireTorpedoes;
                    weight = VALUE * distanceValue;
                }


                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }
            this.data.put(actionTaken, ActionHeadingList);
        }
    }
}
