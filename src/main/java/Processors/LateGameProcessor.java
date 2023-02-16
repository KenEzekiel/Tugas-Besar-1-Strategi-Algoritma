package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.PlayerEffects;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.MathService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class LateGameProcessor extends Processor {
    public LateGameProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        int botSize = bot.getSize();
        if (gameState.foodSpreadDensity() > 40) return;
        var biggestPlayer = gameState.getGameObjects().stream()
                .filter(item -> filterEnemy(item, botSize))
                .sorted(Comparator.comparing(item -> MathService.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        if (biggestPlayer.isEmpty()) {
            return;
        }
        int heading = MathService.getHeadingBetween(bot, biggestPlayer.get(0));
        int weight = 4;
        var ActionHeadingList = new ArrayList<ActionWeight>();
        var actionWeight = new ActionWeight(heading, weight);
        ActionHeadingList.add(actionWeight);
        this.data.put(PlayerActions.Forward, ActionHeadingList);
    }
    boolean filterEnemy(GameObject item, int botSize) {
        if (item.getGameObjectType() != ObjectTypes.PLAYER || item.getId() == bot.getId()) {
            return false;
        }
        if (item.getSize() > botSize) {
            return false;
        }
        return true;
    }

}

