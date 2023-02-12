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
        var nearestFood = gameState.getGameObjects()
                .stream()
                .filter(item -> filterFood(item, obstacle, botPos))
                .min(Comparator.comparing(item -> MathService.getDistanceBetween(botPos, item.getPosition()) - bot.getSize() - item.getSize()));
        if (nearestFood.isPresent()) {
            var array = new ArrayList<ActionWeight>(1);
            this.data.put(PlayerActions.Forward, array);
            var obj = nearestFood.get();
            var actionWeight = new ActionWeight(MathService.getHeadingBetween(botPos, obj.getPosition()), VALUE);
            array.add(actionWeight);
        }
    }


    boolean filterFood(GameObject item, List<GameObject> obstacles, Position botPos) {
        if (item.getGameObjectType() != ObjectTypes.FOOD && item.getGameObjectType() != ObjectTypes.SUPER_FOOD)
            return false;
        // Jangan mengambil food yang di luar map
        if (MathService.getDistanceBetween(item.getPosition(), gameState.world.centerPoint) + bot.getSize() >= gameState.world.radius)
            return false;
        var distance = MathService.getDistanceBetween(botPos, item.getPosition()) - bot.getSize() - item.getSize();
        if (distance < 0) return false;

        for (var obs : obstacles) {
            if (MathService.isCollide(item, obs)) return false;
        }
        return true;
    }

}
