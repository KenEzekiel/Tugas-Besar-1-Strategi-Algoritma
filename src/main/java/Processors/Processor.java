package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Processor {
    protected final GameObject bot;
    protected final GameState gameState;
    protected HashMap<PlayerActions, ArrayList<ActionWeight>> data;


    public Processor(GameObject bot, GameState gameState) {
        this.bot = bot;
        this.gameState = gameState;
        this.data = new HashMap<>();
    }

    public ArrayList<ActionWeight> initializeWeight(PlayerActions action) {
        var arr = new ArrayList<ActionWeight>();
        this.data.put(action, arr);
        return arr;
    }

    public abstract void process();
}
