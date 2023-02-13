package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ShieldProcessor extends Processor {
    public ShieldProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        int distanceTorpedosFromPlayer = 50;
        int minNumOfTorpedos = 3;
        var nearbyHeadingTorpedos = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO && MathService.getDistanceBetween(bot, item) < distanceTorpedosFromPlayer && MathService.isInTorpedoPath(bot, item))
                .collect(Collectors.toList());
        var ActionHeadingList = new ArrayList<ActionWeight>();

        if (nearbyHeadingTorpedos.size() >= minNumOfTorpedos && bot.getSize() > 26) {
            int heading = 0;
            int weight = nearbyHeadingTorpedos.size() * 10;
            var actionWeight = new ActionWeight(heading, weight);
            ActionHeadingList.add(actionWeight);
            this.data.put(PlayerActions.ActivateShield, ActionHeadingList);
        }
    }
}
