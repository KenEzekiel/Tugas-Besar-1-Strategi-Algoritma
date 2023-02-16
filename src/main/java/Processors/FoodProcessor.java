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
        var obstacle = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD).collect(Collectors.toList());
        var filtered = gameState.getGameObjects()
                .stream()
                .filter(item -> filterFood(item, obstacle, bot.getPosition()))
                .collect(Collectors.toList());
        var _nearest = filtered.stream()
                .min(Comparator.comparing(item -> MathService.getDistanceBetween(bot, item)));
        if (_nearest.isEmpty()) {
            return;
        }

        var nearest = _nearest.get();
        var nearestDistance = MathService.getDistanceBetween(bot, nearest);
        var closeToNearest = filtered.stream()
                .filter(item -> !item.getId().equals(nearest.getId()) &&
                        Math.abs(MathService.getDistanceBetween(bot, item) / bot.speed - nearestDistance / bot.speed) <= 3)
                .sorted(Comparator.comparing(item -> MathService.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());


        var array = new ArrayList<ActionWeight>(1);
        this.data.put(PlayerActions.Forward, array);
        var heading = MathService.getHeadingBetween(bot.getPosition(), nearest.getPosition());

        if (closeToNearest.size() > 0) {
            var close = closeToNearest.get(0);
            var heading2 = MathService.getHeadingBetween(bot.getPosition(), close.getPosition());
            var dif = Math.abs(heading - heading2);
            if (dif <= 180) {
                heading += heading < heading2 ? -15 : 15;
            } else {
                dif = 360 - dif;
                heading += heading < heading2 ? 15 : -15;
            }
            heading = (heading + 360) % 360;
            if (dif > 150) {
                var lowestId = closeToNearest.stream().min(Comparator.comparing(a -> a.getId().toString()));
                if (lowestId.isPresent()) {
                    close = lowestId.get();
                }
                var used = close.getId().toString().compareTo(nearest.getId().toString()) > 0 ? close : nearest;
                heading = MathService.getHeadingBetween(bot.getPosition(), used.getPosition());
            }
        }

        var actionWeight = new ActionWeight(heading, VALUE);
        array.add(actionWeight);
    }

    boolean filterFood(GameObject item, List<GameObject> obstacles, Position botPos) {
        if (item.getGameObjectType() != ObjectTypes.FOOD && item.getGameObjectType() != ObjectTypes.SUPER_FOOD && item.getGameObjectType() != ObjectTypes.SUPERNOVA_PICKUP)
            return false;
        var worldCenterDis = MathService.getDistanceBetween(item.getPosition(), gameState.world.centerPoint);
        var curWorldCenterDis = MathService.getDistanceBetween(botPos, gameState.world.centerPoint);

        // Jangan mengambil food yang di luar map
        int threshold = bot.getSpeed() > 1 ? 5 : 20;
        if (gameState.getWorld().getRadius() - worldCenterDis - bot.getSize() - item.getSize() < threshold)
            return false;

        // strategi awal tick ke pinggir map
        if (gameState.world.currentTick <= 75 && worldCenterDis <= Math.min(gameState.world.radius / 2.5, curWorldCenterDis)) {
            return false;
        }
        for (var obs : obstacles) {
            if (MathService.isCollide(item, obs)) return false;
        }
        return true;
    }

}
