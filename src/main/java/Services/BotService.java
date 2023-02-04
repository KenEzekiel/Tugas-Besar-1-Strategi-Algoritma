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

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
//      set player action nya apa aja, ini baru forward
//        playerAction.action = PlayerActions.FORWARD;
        MainProcessor mainProcessor = new MainProcessor(bot, gameState);
        mainProcessor.process();
        playerAction.action = mainProcessor.getBestAction();
        playerAction.heading = mainProcessor.getMaxWeight().getHeading();
//        playerAction.heading = new Random().nextInt(360);


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
