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
    final static double VALUE = 10.0;

    public TorpedoProcessorPlayer(GameObject bot, GameState gameState) {
        super(bot, gameState);
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
        if (bot.getSize() > MINVAL && bot.torpedoSalvoCount > 3) {

            for (GameObject obj : playerList) {
                var objPos = obj.getProjectedPosition();
                double obstacleValue = MathService.calcObjectValueBetweenObjects(gameState.getGameObjects(), bot.getPosition(), obj.getPosition(), 2);
                boolean guarantee = MathService.guaranteeHitTorpedo(bot.getPosition(), obj);
                double hitRate = guarantee ? 1 : 0;
                double weight = (VALUE - obstacleValue - 5) * hitRate;
                int heading = MathService.getHeadingBetween(botPos, objPos);
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }
            this.data.put(PlayerActions.FireTorpedoes, ActionHeadingList);
        }
    }
}
