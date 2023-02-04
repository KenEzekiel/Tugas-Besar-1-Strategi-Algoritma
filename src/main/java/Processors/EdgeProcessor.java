package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Services.MathService;

public class EdgeProcessor extends Processor {
    final static double VALUE = 5.0;

    public EdgeProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getPosition();
        var worldCenter = gameState.world.getCenterPoint();
        double distanceToCenter = MathService.getDistanceBetween(botPos, worldCenter);
        var arr = initializeWeight(PlayerActions.FORWARD);
        var heading = MathService.getHeadingBetween(botPos, worldCenter);
        var weight = VALUE / (gameState.world.getRadius() - distanceToCenter - bot.getSize());
        arr.add(new ActionWeight(heading, Math.min(5, weight)));
    }
}
