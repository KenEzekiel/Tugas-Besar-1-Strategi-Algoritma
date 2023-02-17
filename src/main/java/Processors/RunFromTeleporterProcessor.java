package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.MathService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RunFromTeleporterProcessor extends Processor{
    public RunFromTeleporterProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        var biggestPlayer = gameState.getPlayerGameObjects().stream()
                .filter(item -> item.id != bot.id)
                .max(Comparator.comparing(item -> item.size));
        if (biggestPlayer.isEmpty()) {
            return;
        }
        var tlpList = gameState.getGameObjects().stream()
                .filter(item -> item.gameObjectType == ObjectTypes.TELEPORTER && item.getCurrentHeading() == MathService.getHeadingBetween(item, bot) && MathService.guaranteeHitTorpedo(item.getPosition(), bot) && MathService.getDistanceBetween(bot, item) < 50)
                .collect(Collectors.toList());

        var ActionHeadingList = new ArrayList<ActionWeight>();
        var tlp = bot.firedTeleport;

        int threshold = 15;
        if (tlpList.size() > 0) {
            var obj = tlpList.get(0);
            // firing teleport
            if (MathService.getDistanceBetween(bot, obj) <= biggestPlayer.get().getSize() + threshold) {
                // determine the fastest way to get out, except to the edge position
                double weight = 605;
                // default is to the center of the map
                int heading;
                // find a better way except to the edge
                int altHeading;
                // Check the heading perpendicular to the obstacle
                int altLeft = MathService.getHeadingBetween(bot, obj) + 90 % 360;
                int altRight = MathService.getHeadingBetween(bot, obj) - 90 % 360;
                // Check a radius from a distance from the bot for the value inside it
                int radius = bot.size;
                int pointDistance = biggestPlayer.get().getSize();
                int altVal;
                Position leftPos = MathService.getPositionFromAPoint(bot.getPosition(), pointDistance, altLeft);
                int leftVal = MathService.calcObjectValueInArea(gameState, leftPos, radius, bot);
                Position rightPos = MathService.getPositionFromAPoint(bot.getPosition(), pointDistance, altRight);
                int rightVal = MathService.calcObjectValueInArea(gameState, rightPos, radius, bot);

                // Check which val is greater, and whether it is not headed to the edge
                if (leftVal >= rightVal) {
                    altHeading = altLeft;
                    altVal = leftVal;
                    if (altHeading == MathService.reverseHeading(MathService.getHeadingBetween(bot.getPosition(), gameState.getWorld().getCenterPoint()))) {
                        altHeading = MathService.reverseHeading(altRight);
                        altVal = rightVal;
                    }
                } else {
                    altHeading = altRight;
                    altVal = rightVal;
                    if (altHeading == MathService.reverseHeading(MathService.getHeadingBetween(bot.getPosition(), gameState.getWorld().getCenterPoint()))) {
                        altHeading = MathService.reverseHeading(altLeft);
                        altVal = leftVal;
                    }
                }

                heading = altHeading;


                if (!bot.hasJustFireTeleport && bot.teleporterCount > 0 && tlp == null && bot.getSize() > 15 + 20) {
                    var actionWeight = new ActionWeight(heading, weight);
                    ActionHeadingList.add(actionWeight);
                    this.data.put(PlayerActions.FireTeleport, ActionHeadingList);
                }
            }

            if (tlp == null) {
                return;
            }
            int buffer = 5;
            if (MathService.getDistanceBetween(obj, tlp) >= bot.size + buffer + biggestPlayer.get().size) {
                int heading = 0;
                double weight = 4999;
                var actionWeight = new ActionWeight(heading, weight);
                ActionHeadingList.add(actionWeight);
                this.data.put(PlayerActions.Teleport, ActionHeadingList);
            }
        }

    }
}
