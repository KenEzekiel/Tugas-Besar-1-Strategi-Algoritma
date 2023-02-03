package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameState;

import java.util.ArrayList;

public class MainProcessor extends Processor {
    public MainProcessor(GameState gameState) {
        super(gameState);
    }

    public void process() {
        FoodProcessor foodProcessor = new FoodProcessor(gameState);
        foodProcessor.process();
        ArrayList<ActionWeight> w = foodProcessor.data.get(PlayerActions.FORWARD);
    }
}
