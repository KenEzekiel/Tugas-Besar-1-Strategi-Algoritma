package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Models.Vector;
import Services.MathService;
import com.azure.core.annotation.Post;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class EnemyProcessor extends Processor{
    public EnemyProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override

    public void process() {
        Position botPos = bot.getPosition();
        int botSize = bot.getSize();
        int torpedoSpeed = 20;
        int buffer = 60;
        int reactionDistance = botSize * botSize * torpedoSpeed / 200 + buffer;
        if (botSize < 50) {
            var detectedTorpedos = gameState.getGameObjects().stream()
                    .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO
                            && MathService.getDistanceBetween(bot, item) <= reactionDistance
                            && MathService.isInTorpedoPath(bot, item))
                    .collect(Collectors.toList());
            var ActionHeadingList = new ArrayList<ActionWeight>();
            //        double foodDensity = gameState.foodSpreadDensity();

            var _nearestTorpedos = detectedTorpedos.stream()
                    .min(Comparator.comparing(item -> MathService.getDistanceBetween(botPos, item.getPosition()) - bot.getSize() - item.getSize()));
            if (_nearestTorpedos.isEmpty()) {
                return;
            }
            var nearestTorpedos = _nearestTorpedos.get();
            if (MathService.distanceToTorpedoPath(bot, nearestTorpedos) < botSize + 10) {
                int heading = MathService.torpedoEscapeHeading(bot, nearestTorpedos);
                int weight = 400000;
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
                this.data.put(PlayerActions.Forward, ActionHeadingList);
            }

        }
    }




}
