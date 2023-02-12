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

public class TeleportProcessor extends Processor {
    public TeleportProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var playerList = gameState.getPlayerGameObjects()
                .stream().filter(item -> (item.id != bot.id))
                .collect(Collectors.toList());

        var ActionHeadingList = new ArrayList<ActionWeight>();

        // Fire teleport

        if (bot.teleporterCount > 0 && bot.getSize() > 15 + 20) {
             for (GameObject ply : playerList) {
                 if (ply.getSize() <= bot.getSize() * 0.7) {
                     int heading = MathService.getHeadingBetween(bot, ply);
                     // weighting
                     boolean guarantee = MathService.guaranteeHitTorpedo(bot.getPosition(), ply);
                     double hitRate = guarantee ? 1 : 0.2;
                     double weight = ply.getSize() * hitRate;
                     var actionWeight = new ActionWeight(heading, weight);
                     ActionHeadingList.add(actionWeight);
                 }
            }
             this.data.put(PlayerActions.FireTeleporter, ActionHeadingList);
        }

        var teleportList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                .collect(Collectors.toList());

        for (GameObject tlp : teleportList) {
            var playerInRadius = MathService.getObjectsInArea(gameState, tlp.getPosition(), bot.size / 2)
                    .stream().filter(item -> item.object.getGameObjectType() == ObjectTypes.PLAYER)
                    .sorted(Comparator.comparing(item -> item.distance))
                    .collect(Collectors.toList());
            if (!playerInRadius.isEmpty()) {
                var numOfPlayer = playerInRadius.size();
                int heading = 0;
                double weight = 20;
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
            }
            this.data.put(PlayerActions.Teleport, ActionHeadingList);
        }
    }
}
