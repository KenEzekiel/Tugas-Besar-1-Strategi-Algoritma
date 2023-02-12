package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.MathService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class EnemyProcessor extends Processor{
    public EnemyProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var botPos = bot.getPosition();
        var playerList = gameState.getPlayerGameObjects()
                .stream().filter(item -> (item.id != bot.id))
                .sorted(Comparator.comparing(item -> MathService.getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

    }




}
