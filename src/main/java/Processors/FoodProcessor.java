package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.PlayerEffects;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class FoodProcessor extends Processor {
    final static double VALUE = 5.0;
    final static double SUPER_VALUE = 5.0;


    public FoodProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getProjectedPosition();
        var foodList = gameState.getGameObjects()
                .stream()
                .filter(this::filterFood)
                .sorted(Comparator.comparing(item -> MathService.getDistanceBetween(botPos, item.getPosition()) - bot.getSize() - item.getSize()))
                .collect(Collectors.toList());

        var array = new ArrayList<ActionWeight>(1);
        if (foodList.size() != 0) {
            this.data.put(PlayerActions.Forward, array);
            double worldDiameter = gameState.getWorld().radius * 2;
            var isSuperFood = bot.effects.getState(PlayerEffects.SUPERFOOD);
            for (GameObject obj : foodList) {
                var value = VALUE;
                if (isSuperFood && obj.getGameObjectType() == ObjectTypes.SUPER_FOOD) {
                    value = SUPER_VALUE;
                }
                var distance = MathService.getDistanceBetween(botPos, obj.getPosition()) - bot.getSize() - obj.getSize();
//              jika pada proyeksi makanan sudah diambil, tidak perlu
                if (distance < 0)
                    continue;
                double weight = value * Math.pow((worldDiameter - distance) / worldDiameter, 2.0);
                var actionWeight = new ActionWeight(MathService.getHeadingBetween(botPos, obj.getPosition()), weight);
                array.add(actionWeight);
            }
        }

    }


    boolean filterFood(GameObject item) {
        if (item.getGameObjectType() != ObjectTypes.FOOD && item.getGameObjectType() != ObjectTypes.SUPER_FOOD)
            return false;
        // Jangan mengambil food yang di luar map
        if (MathService.getDistanceBetween(item.getPosition(), gameState.world.centerPoint) + 20 >= gameState.world.radius)
            return false;
        return true;
    }

}
