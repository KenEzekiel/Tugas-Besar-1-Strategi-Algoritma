package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.PlayerEffects;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodProcessor extends Processor {
    final static double VALUE = 5.0;
    final static double SUPER_VALUE = 6.0;


    public FoodProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getProjectedPosition(1);
        var obstacle = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD).collect(Collectors.toList());
        var foodList = gameState.getGameObjects()
                .stream()
                .filter(item -> filterFood(item, obstacle))
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


    boolean filterFood(GameObject item, List<GameObject> obstacles) {
        if (item.getGameObjectType() != ObjectTypes.FOOD && item.getGameObjectType() != ObjectTypes.SUPER_FOOD)
            return false;
        // Jangan mengambil food yang di luar map
        if (MathService.getDistanceBetween(item.getPosition(), gameState.world.centerPoint) + 20 >= gameState.world.radius)
            return false;

        for (var obs : obstacles) {
            if (MathService.isCollide(item, obs)) return false;
        }
        return true;
    }

}
