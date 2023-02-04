package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.MathService;

import java.util.ArrayList;

public class EdgeProcessor extends Processor {
    final static double VALUE = 20.0;

    public EdgeProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getPosition();
        var worldCenter = new Position(0, 0);
        double distanceToCenter = MathService.getDistanceBetween(botPos, worldCenter);
        var arr = new ArrayList<ActionWeight>();
        var heading = MathService.getHeadingBetween(botPos, worldCenter);
        var weight = VALUE / (gameState.getWorld().getRadius() - distanceToCenter - bot.getSize());
        weight = Math.min(VALUE, weight);
        if (weight < 0) {
            weight = 1000;
        }
        arr.add(new ActionWeight(heading, weight));
        this.data.put(PlayerActions.FORWARD, arr);
    }
}
