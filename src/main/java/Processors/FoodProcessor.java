package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FoodProcessor extends Processor {
    final static double VALUE = 5.0;
    final static double SUPER_VALUE = 7.5;


    public FoodProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var foodList = gameState.getGameObjects()
                .stream()
                .filter(this::filterFood)
                .collect(Collectors.toList());

        var array = new ArrayList<ActionWeight>(1);
        if (foodList.size() != 0) {
            this.data.put(PlayerActions.FORWARD, array);
            for (GameObject obj : foodList) {
                var value = obj.getGameObjectType() == ObjectTypes.FOOD ? VALUE : SUPER_VALUE;
                double weight = value / MathService.getDistanceBetween(bot, obj);
                var actionWeight = new ActionWeight(MathService.getHeadingBetween(bot, obj), weight);
                array.add(actionWeight);
            }
        }
    }


    boolean filterFood(GameObject item) {
        if (item.getGameObjectType() != ObjectTypes.FOOD && item.getGameObjectType() != ObjectTypes.SUPER_FOOD)
            return false;
        // Jangan mengambil food yang di luar map
        if (MathService.getDistanceBetween(item.getPosition(), gameState.world.centerPoint) >= gameState.world.radius)
            return false;
        return true;
    }

}
