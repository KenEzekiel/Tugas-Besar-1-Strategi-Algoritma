package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.PlayerEffects;
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
        int sizeMinBot = 50;
        int distanceTorpedosFromPlayer = 180;
        int criticalDistance = 35;
        int minNumOfTorpedos = 2;
        boolean existCriticalTorpedos = false;
        var nearbyHeadingTorpedos = gameState.getGameObjects().stream()
                .filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO
                        && MathService.getDistanceBetween(bot, item) <= distanceTorpedosFromPlayer
                        && MathService.isInTorpedoPath(bot, item))
                .collect(Collectors.toList());
        var ActionHeadingList = new ArrayList<ActionWeight>();

        for (GameObject torpedo : nearbyHeadingTorpedos) {
            if (MathService.getDistanceBetween(bot, torpedo) <= criticalDistance) {
                existCriticalTorpedos = true;
                break;
            }
        }

        if (existCriticalTorpedos && nearbyHeadingTorpedos.size() >= minNumOfTorpedos && bot.getSize() > sizeMinBot) {
            int heading = 0;
            int weight = 400000;
            var actionWeight = new ActionWeight(heading, weight);
            ActionHeadingList.add(actionWeight);
            this.data.put(PlayerActions.ActivateShield, ActionHeadingList);
        }
    }
}
