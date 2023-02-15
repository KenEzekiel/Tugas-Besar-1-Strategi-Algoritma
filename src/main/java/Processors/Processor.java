package Processors;

import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Processor extends Thread {
    protected final GameObject bot;
    protected final GameState gameState;
    protected boolean processed = false;
    protected HashMap<PlayerActions, ArrayList<ActionWeight>> data;


    public Processor(GameObject bot, GameState gameState) {
        this.bot = bot;
        this.gameState = gameState;
        this.data = new HashMap<>();
    }

    public abstract void process();

    public void run() {
        this.process();
    }
}
