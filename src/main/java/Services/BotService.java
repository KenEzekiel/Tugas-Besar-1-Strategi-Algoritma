package Services;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Processors.MainProcessor;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    private GameObject firedTeleport;
    private boolean hasJustFiredTeleport;
    private int teleportHeading;

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
        if (this.hasJustFiredTeleport) {
            var nearestTeleport = gameState.getGameObjects()
                                                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                                                    .min(Comparator.comparing(item -> MathService.getDistanceBetween(bot.getPosition(), item.getPosition())));
            if (nearestTeleport.isPresent()) {
                this.firedTeleport = nearestTeleport.get();
            }
        }
        MainProcessor mainProcessor = new MainProcessor(bot, gameState);
        mainProcessor.process();
        playerAction.action = mainProcessor.getBestAction();
        playerAction.heading = mainProcessor.getMaxWeight().getHeading();

        this.playerAction = playerAction;
        if (playerAction.action == PlayerActions.FireTeleporter) {
            this.hasJustFiredTeleport = true;
            this.teleportHeading = playerAction.heading;
        } else {
            this.hasJustFiredTeleport = false;
            this.teleportHeading = 0;
        }
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
