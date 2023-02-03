package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;

import java.util.ArrayList;

public class MainProcessor extends Processor {
    public MainProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    public void process() {
        FoodProcessor foodProcessor = new FoodProcessor(bot, gameState);
        foodProcessor.process();
        ArrayList<ActionWeight> w = foodProcessor.data.get(PlayerActions.FORWARD);
    }
}
