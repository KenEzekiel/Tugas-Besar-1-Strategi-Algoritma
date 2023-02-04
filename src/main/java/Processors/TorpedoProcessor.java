package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TorpedoProcessor extends Processor {

    // MINVAL is 16 to prevent self destruct
    final static double MINVAL = 16.0;
    final static double VALUE = 1.0;

    public TorpedoProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }


    private double calculateHitRate(double distance) {
        return 1 - (distance / gameState.getWorld().getRadius());
    }

    @Override
    public void process() {
        // player nearest
        var playerList = gameState.getPlayerGameObjects()
                .stream().filter(item -> (item.id != bot.id))
                .collect(Collectors.toList());
        var ActionHeadingList = new ArrayList<ActionWeight>();

        // Only trigger if the player has more than some number MINVAL
        // Weight will be + if player's salvo is nearing max
        if (bot.getSize() > MINVAL && bot.torpedoSalvoCount > 0) {
            var _avgSize = playerList.stream().mapToInt(GameObject::getSize).average();
            var avgSize = _avgSize.isPresent() ? _avgSize.getAsDouble() : 0;

            for (GameObject obj : playerList) {

                double distance = MathService.getDistanceBetween(bot, obj);
//                double obstacleValue = ;
                double hitRate = calculateHitRate(distance);
                // 0.9 is priority value
                double sizeValue = bot.getSize() < avgSize ? 0.9 : 1;
                // 1.2 is priority value
                double salvoValue = bot.torpedoSalvoCount == 5 ? 1.2 : 1;
                // Can be changed, how near, or how many obstacles is in the way?
                double weight = (VALUE * 10) * hitRate * sizeValue * salvoValue - 5;
                // TODO : heading is to be redirected with projected point of time
                int heading = MathService.getHeadingBetween(bot, obj);
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }
            this.data.put(PlayerActions.FireTorpedoes, ActionHeadingList);
        }
    }
}
