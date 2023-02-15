import Enums.ObjectTypes;
import Enums.PlayerEffects;
import Models.GameObject;
import Models.GameState;
import Models.GameStateDto;
import Models.Position;
import Services.BotService;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Main.class);
        BotService botService = new BotService();
        String token = System.getenv("Token");
        token = (token != null) ? token : UUID.randomUUID().toString();

        String environmentIp = System.getenv("RUNNER_IPV4");

        String ip = (environmentIp != null && !environmentIp.isBlank()) ? environmentIp : "localhost";
        ip = ip.startsWith("http://") ? ip : "http://" + ip;

        String url = ip + ":" + "5000" + "/runnerhub";

        HubConnection hubConnection = HubConnectionBuilder.create(url)
                .build();

        hubConnection.on("Disconnect", (id) -> {
            System.out.println("Disconnected:");

            hubConnection.stop();
        }, UUID.class);

        hubConnection.on("Registered", (id) -> {
            System.out.println("Registered with the runner " + id);

            Position position = new Position();
            GameObject bot = new GameObject(id, 10, 20, 0, position, ObjectTypes.PLAYER, new PlayerEffects(0),
                    0, 0, 1, 0);
            botService.setBot(bot);
        }, UUID.class);

        AtomicLong lastSendAction = new AtomicLong(System.currentTimeMillis());
        AtomicLong lastNewState = new AtomicLong(lastSendAction.get() - 1);

        hubConnection.on("ReceiveGameState", (gameStateDto) -> {
            GameState gameState = new GameState();
            gameState.world = gameStateDto.getWorld();

            for (Map.Entry<String, List<Integer>> objectEntry : gameStateDto.getGameObjects().entrySet()) {
                gameState.getGameObjects().add(GameObject.FromStateList(UUID.fromString(objectEntry.getKey()), objectEntry.getValue()));
            }

            for (Map.Entry<String, List<Integer>> objectEntry : gameStateDto.getPlayerObjects().entrySet()) {
                gameState.getPlayerGameObjects().add(GameObject.FromStateList(UUID.fromString(objectEntry.getKey()), objectEntry.getValue()));
            }

            botService.setGameState(gameState);
            lastNewState.set(System.currentTimeMillis());
        }, GameStateDto.class);

        hubConnection.start().blockingAwait();

        Thread.sleep(1000);
        System.out.println("Registering with the runner...");
        hubConnection.send("Register", token, "AFK Bot");

        //This is a blocking call
        hubConnection.start().subscribe(() -> {
            while (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {

                GameObject bot = botService.getBot();
                if (bot == null || lastSendAction.get() > lastNewState.get()) {
                    continue;
                }

                botService.getPlayerAction().setPlayerId(bot.getId());
                botService.computeNextPlayerAction(botService.getPlayerAction());
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    lastSendAction.set(System.currentTimeMillis());
                    hubConnection.send("SendPlayerAction", botService.getPlayerAction());
                }
            }
        });

        hubConnection.stop();
    }
}
