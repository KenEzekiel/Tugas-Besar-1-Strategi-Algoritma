package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TorpedoProcessorPlayer extends Processor {

    // MINVAL is 16 to prevent self destruct
    final static double MINVAL = 16.0;
    final static double VALUE = 1.0;

    public TorpedoProcessorPlayer(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }


    private double calculateHitRate(double distance) {
        var worldDiameter = gameState.getWorld().getRadius() * 2;
        return (worldDiameter - distance) / worldDiameter;
    }

    @Override
    public void process() {
        var botPos = bot.getProjectedPosition();
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
                var objPos = obj.getProjectedPosition();
                double distance = MathService.getDistanceBetween(botPos, objPos) - bot.getSize() - obj.getSize();
                double obstacleValue = MathService.calcObjectValueBetweenObjects(gameState, bot, obj, 5);
                double hitRate = calculateHitRate(distance);
                // 0.9 is priority value
                double sizeValue = obj.getSize() < avgSize ? 1 : 1.5;
                // 1.2 is priority value
                double salvoValue = bot.torpedoSalvoCount == 5 ? 1.1 : 1;
                // Can be changed, need a function design
                double weight = (VALUE * 10) * hitRate * sizeValue * salvoValue - obstacleValue;
                // TODO : heading is to be redirected with projected point of time
                int heading = MathService.getHeadingBetween(botPos, objPos);
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }
            this.data.put(PlayerActions.FireTorpedoes, ActionHeadingList);
        }
    }
}