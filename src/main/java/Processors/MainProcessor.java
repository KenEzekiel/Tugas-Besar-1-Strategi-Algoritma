package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;

import java.util.ArrayList;

public class MainProcessor extends Processor {
    public MainProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    public void process() {
    }

    public void process(PlayerAction playerAction) {
        // Process bisa dianalogikan sebagai sort action didalam algoritma greedy
        FoodProcessor foodProcessor = new FoodProcessor(bot, gameState);
        foodProcessor.process();
        var w = foodProcessor.data.get(PlayerActions.FORWARD);

        playerAction.action = PlayerActions.FORWARD;
        playerAction.heading = w.get(0).getHeading();
    }
}
