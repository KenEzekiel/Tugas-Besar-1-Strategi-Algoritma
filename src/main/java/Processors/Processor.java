package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameState;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Processor {
    protected final GameState gameState;
    protected HashMap<PlayerActions, ArrayList<ActionWeight>> data;


    public Processor(GameState gameState) {
        this.gameState = gameState;
        this.data = new HashMap<>();
    }

    public abstract void process();
}
