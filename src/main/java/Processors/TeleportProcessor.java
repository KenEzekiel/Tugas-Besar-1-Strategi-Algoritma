package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
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
        var tlp = bot.firedTeleport;


        // Fire teleport

        if (!bot.hasJustFireTeleport && bot.teleporterCount > 0 && tlp == null && bot.getSize() > 15 + 20) {
            for (GameObject ply : playerList) {
                if (ply.getSize() > 30 && ply.getSize() <= (bot.getSize() - 20) - 20) {
                    int heading = MathService.getHeadingBetween(bot, ply);
                    // weighting
                    boolean guarantee = MathService.guaranteeHitTorpedo(bot.getPosition(), ply);
                    double hitRate = guarantee ? 1 : 0.2;
                    double weight = ply.getSize() * hitRate;
                    var actionWeight = new ActionWeight(heading, weight);
                    ActionHeadingList.add(actionWeight);
                }
            }
            this.data.put(PlayerActions.FireTeleport, ActionHeadingList);
        }
        if (tlp == null) return;
        int buffer = 5;
        var playerInRadius = MathService.getObjectsInArea(gameState, tlp.getPosition(), bot.getSize())
                .stream().filter(item ->
                        item.object.getGameObjectType() == ObjectTypes.PLAYER && item.object.getSize() + buffer < bot.getSize())
                .collect(Collectors.toList());
        if (!playerInRadius.isEmpty()) {
            var numOfPlayer = playerInRadius.size();
            int heading = 0;
            double weight = 5000;
            var actionWeight = new ActionWeight(heading, weight);
            ActionHeadingList.add(actionWeight);
        }
        this.data.put(PlayerActions.Teleport, ActionHeadingList);
    }
}
