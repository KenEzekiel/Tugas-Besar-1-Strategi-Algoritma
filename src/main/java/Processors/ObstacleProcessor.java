package Processors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.ActionWeight;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.MathService;
import Services.ObjectDistanceDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class ObstacleProcessor extends Processor {
    final static double VALUE = 5.0;

    public ObstacleProcessor(GameObject bot, GameState gameState) {
        super(bot, gameState);
    }

    @Override
    public void process() {
        if (!gameState.getGameObjects().isEmpty()) {
            var botPos = bot.getProjectedPosition();
            var obstacleList = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.GAS_CLOUD))
                    .collect(Collectors.toList());
            var ActionHeadingList = new ArrayList<ActionWeight>();
            double worldDiameter = gameState.getWorld().radius * 2;
            for (GameObject obj : obstacleList) {
                int heading;
                var distance = MathService.getDistanceBetween(botPos, obj.getPosition()) - bot.getSize() - obj.getSize();
                if (distance <= 6) {
                    double weight = 500;
                    if (distance < 3 && distance >= 0) {
                        weight = 600;
                        heading = MathService.reverseHeading(MathService.getHeadingBetween(botPos, obj.getPosition()));
                    } else if (distance < 0) {
                        // determine the fastest way to get out, except to the edge position
                        weight = 605;
                        // default is to the center of the map
                        heading = MathService.getHeadingBetween(bot.getPosition(), gameState.getWorld().getCenterPoint());
                        // find a better way except to the edge
                        int altHeading;
                        // Check the heading perpendicular to the obstacle
                        int altLeft = MathService.getHeadingBetween(bot, obj) + 90 % 360;
                        int altRight = MathService.getHeadingBetween(bot, obj) - 90 % 360;

                        if (MathService.getDegreeDifference(heading, altLeft) < 30) {
                            altLeft = heading;
                        } else if (MathService.getDegreeDifference(heading, altRight) < 30) {
                            altRight = heading;
                        }
                        // Check a radius from a distance from the bot for the value inside it
                        int radius = 10;
                        int pointDistance = 5 + bot.getSize();
                        Position defPos = MathService.getPositionFromAPoint(bot.getPosition(), pointDistance, heading);
                        int defVal = MathService.calcObjectValueInArea(gameState, defPos, radius, bot);
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

                        // Check, is it better to go left/right or still go straight to center?
                        if (defVal >= altVal) {
                            // Still default
                        } else {
                            // Switch to left/right if it is not to the edge
                            heading = altHeading;
                        }

                    } else {
                        heading = (MathService.getHeadingBetween(botPos, obj.getPosition()) + 90) % 360;
                    }
                    // weight akan berpengaruh pada prioritas algoritma untuk mengambil action ini

                    var actionWeight = new ActionWeight(heading, weight);
                    ActionHeadingList.add(actionWeight);
                }
            }

            this.data.put(PlayerActions.Forward, ActionHeadingList);
        }
    }
}
