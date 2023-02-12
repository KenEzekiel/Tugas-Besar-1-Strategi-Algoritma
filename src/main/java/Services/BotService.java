package Services;

import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Processors.MainProcessor;

import java.util.Optional;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    private GameObject firedTeleport;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public void setFiredTeleport(GameObject teleport) { this.firedTeleport = teleport; }

    public GameObject getFiredTeleport() { return this.firedTeleport; }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        MainProcessor mainProcessor = new MainProcessor(bot, gameState);
        mainProcessor.process();
        playerAction.action = mainProcessor.getBestAction();
        playerAction.heading = mainProcessor.getMaxWeight().getHeading();

        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }


}
