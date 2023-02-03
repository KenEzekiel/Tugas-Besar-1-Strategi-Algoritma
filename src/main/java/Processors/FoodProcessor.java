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

public class FoodProcessor extends Processor {
    final static double VALUE = 5.0;


    public FoodProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType() == ObjectTypes.SUPER_FOOD)
                .sorted(Comparator.comparing(item -> MathService.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());
        var array = new ArrayList<ActionWeight>(1);
        if (foodList.size() == 0) {
            this.data.put(PlayerActions.STOP, array);
            array.add(new ActionWeight(0, 5));
        } else {
            this.data.put(PlayerActions.FORWARD, array);
            for (GameObject obj : foodList) {
                var value = obj.getGameObjectType() == ObjectTypes.FOOD ? VALUE : VALUE * 1.5;
                double weight = value / MathService.getDistanceBetween(bot, obj);
                var actionWeight = new ActionWeight(MathService.getHeadingBetween(bot, obj), weight);
                array.add(actionWeight);
            }
        }
    }

}
