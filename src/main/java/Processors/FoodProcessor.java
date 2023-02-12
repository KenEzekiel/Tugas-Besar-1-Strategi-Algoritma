package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.MathService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FoodProcessor extends Processor {
    final static double VALUE = 3.0;


    public FoodProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getPosition();
        var obstacle = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD).collect(Collectors.toList());
        var filtered = gameState.getGameObjects()
                .stream()
                .filter(item -> filterFood(item, obstacle, botPos))
                .collect(Collectors.toList());
        var _nearest = filtered.stream()
                .min(Comparator.comparing(item -> MathService.getDistanceBetween(bot, item)));
        if (_nearest.isEmpty()) {
            return;
        }
        var nearest = _nearest.get();

        var array = new ArrayList<ActionWeight>(1);
        this.data.put(PlayerActions.Forward, array);
        var heading = MathService.getHeadingBetween(botPos, nearest.getPosition());

        if (MathService.getDegreeDifference(heading, bot.currentHeading) > 120) {
            heading = bot.currentHeading;
        }
        // TODO: cek di currentHeading ada food atau ngga, kasih treshold misal 30 derajat
        var actionWeight = new ActionWeight(heading, VALUE);
        array.add(actionWeight);
    }


    boolean filterFood(GameObject item, List<GameObject> obstacles, Position botPos) {
        if (item.getGameObjectType() != ObjectTypes.FOOD && item.getGameObjectType() != ObjectTypes.SUPER_FOOD)
            return false;
        // Jangan mengambil food yang di luar map
        if (MathService.getDistanceBetween(item.getPosition(), gameState.world.centerPoint) + bot.getSize() >= gameState.world.radius)
            return false;
        for (var obs : obstacles) {
            if (MathService.isCollide(item, obs)) return false;
        }
        return true;
    }

}
