package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.MathService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class TorpedoProcessorObstacle extends Processor {
    final static double MINVAL = 16.0;
    final static double VALUE = 5.0;

    public TorpedoProcessorObstacle(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        // player nearest
        var gasCloudList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                .sorted(Comparator
                        .comparing(item -> MathService.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        var ActionHeadingList = new ArrayList<ActionWeight>();
        PlayerActions actionTaken = PlayerActions.Forward;

        // Only trigger if the player has more than some number MINVAL
        // Weight will be + if player's salvo is nearing max
        if (bot.getSize() > MINVAL && bot.torpedoSalvoCount > 0) {

            for (GameObject obj : gasCloudList) {
                int heading = MathService.getHeadingBetween(bot, obj);
                double distanceValue = MathService.getDistanceBetween(bot, obj) != 0 ? 1 + MathService.getDistanceBetween(bot, obj) / 1000 : 5;
                // 1.2 and 0.3 is still an arbitrary value
                double headingValue = bot.currentHeading == heading ? 1.2 : 0.3;
                double groupValue;

                // Can be changed, how near, how grouped, how big
                double weight;

                boolean biggerThanHalf = obj.getSize() > bot.getSize() * 0.5;
                if (biggerThanHalf) {
                    actionTaken = PlayerActions.Forward;
                    heading = heading * (-1);
                    weight = distanceValue;
                } else {
                    actionTaken = PlayerActions.FireTorpedoes;
                    weight = VALUE * headingValue * distanceValue;
                }


                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }

            this.data.put(actionTaken, ActionHeadingList);
        }
    }
}
