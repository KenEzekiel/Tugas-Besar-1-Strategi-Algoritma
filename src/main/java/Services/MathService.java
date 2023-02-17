package Services;

import Enums.ObjectTypes;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Models.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MathService {
    public static double getDistanceBetween(GameObject object1, GameObject object2) {
        return getDistanceBetween(object1.getPosition(), object2.getPosition()) - object1.size - object2.size;
    }

    public static double getDistanceBetween(double x1, double y1, double x2, double y2) {
        var dx = Math.abs(x2 - x1);
        var dy = Math.abs(y2 - y1);
        return Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    public static double getDistanceBetween(Position p1, Position p2) {
        return getDistanceBetween(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static int getHeadingBetween(GameObject bot, GameObject otherObject) {
        return getHeadingBetween(bot.getPosition(), otherObject.getPosition());
    }

    public static int getHeadingBetween(Position from, Position target) {
        var direction = toDegrees(Math.atan2(target.getY() - from.getY(), target.getX() - from.getX()));
        return (direction + 360) % 360;
    }

    public static int toDegrees(double v) {
        return (int) Math.round(v * (180 / Math.PI));
    }

    public static int reverseHeading(int Heading) {
        return ((Heading + 180) % 360);
    }

    public static ArrayList<ObjectDistanceDto> getObjectsInArea(GameState state, Position pos, int radius) {
        var objects = state.getGameObjects().stream()
                .map(obj -> new ObjectDistanceDto(obj, getDistanceBetween(pos, obj.getPosition())))
                .filter(item -> item.distance < radius + item.object.getSize()
                ).collect(Collectors.toList());
        var players = state.getPlayerGameObjects().stream()
                .map(obj -> new ObjectDistanceDto(obj, getDistanceBetween(pos, obj.getPosition())))
                .filter(item -> item.distance < radius + item.object.getSize()
                ).collect(Collectors.toList());
        ArrayList<ObjectDistanceDto> res = new ArrayList<>(objects.size() + players.size());
        res.addAll(objects);
        res.addAll(players);
        return res;
    }

    public static int calcObjectValueInArea(GameState state, Position pos, int radius, GameObject bot) {
        ArrayList<ObjectDistanceDto> objectDistanceDtos = getObjectsInArea(state, pos, radius);
        int val = 0;
        for (ObjectDistanceDto lObj : objectDistanceDtos) {
            if (lObj.object.getGameObjectType() == ObjectTypes.GAS_CLOUD) {
                val -= lObj.object.getSize();
            } else if (lObj.object.getGameObjectType() == ObjectTypes.FOOD) {
                val += 3;
            } else if (lObj.object.getGameObjectType() == ObjectTypes.PLAYER) {
                if (lObj.object.getSize() > 0.9 * bot.getSize()) {
                    val -= bot.getSize();
                } else {
                    val += lObj.object.getSize();
                }
            }
            if (lObj.object.getGameObjectType() == ObjectTypes.SUPER_FOOD) {
                val += 5;
            }
            if (lObj.object.getGameObjectType() == ObjectTypes.ASTEROID_FIELD) {
                val -= 2;
            }
            if (lObj.object.getGameObjectType() == ObjectTypes.SUPERNOVA_PICKUP) {
                val += 4;
            }
        }
        return val;
    }

    public static Position getPositionFromAPoint(Position pos1, int distance, int degree) {
        int x = (int) Math.round(distance * Math.cos(degree) + pos1.getX());
        int y = (int) Math.round(distance * Math.sin(degree) + pos1.getY());
        Position ret = new Position(x, y);
        return ret;
    }

    public static int calcObjectValueBetweenObjects(List<GameObject> objects, Position pos1, Position pos2, int threshold) {
        Vector refVector = new Vector(pos1, pos2);
        return objects.stream()
                .filter(obj -> obj.getGameObjectType() == ObjectTypes.FOOD ||
                        obj.getGameObjectType() == ObjectTypes.GAS_CLOUD ||
                        obj.getGameObjectType() == ObjectTypes.ASTEROID_FIELD ||
                        obj.getGameObjectType() == ObjectTypes.WORMHOLE ||
                        obj.getGameObjectType() == ObjectTypes.SUPER_FOOD)
                .filter(obj -> obj.isObjectCloseToVector(pos1, pos2, 2))
                .mapToInt(GameObject::getSize).sum();

    }

    public static boolean guaranteeHitTorpedo(Position firePos, GameObject targetPlayer) {
        double modifierConstant = 1.5;
        int torpedoSpeed = 20;
        double shipSize = (double) targetPlayer.getSize();
        double distance = getDistanceBetween(firePos, targetPlayer.getPosition());
        int size = targetPlayer.getSize();
        return (200 * distance <= shipSize * shipSize * torpedoSpeed * modifierConstant);
    }


    public static boolean guaranteeHitTeleport(GameObject bot, GameObject enemy) {
        double modifierConstant = 1.5;
        int torpedoSpeed = 20;
        double distance = getDistanceBetween(bot, enemy);
        return (200 * distance <= bot.getSize() * enemy.getSize() * torpedoSpeed * modifierConstant);
    }

    public static boolean isCollide(GameObject obj1, GameObject obj2) {
        return getDistanceBetween(obj1, obj2) < 0;
    }

    public static int getDegreeDifference(int degree1, int degree2) {
        var dif = Math.abs(degree1 - degree2);
        if (dif <= 180) {
            return dif;
        }
        return 360 - dif;
    }

    public static boolean isInTorpedoPath(GameObject bot, GameObject torpedo) {
        int sensitivity = 2;
        Vector botPosVector = new Vector(bot.getPosition());
        Vector relativeBotPosVector = Vector.minus(botPosVector, new Vector(torpedo.getPosition()));
        Vector torpedoVector = new Vector(torpedo.getCurrentHeading());
        if (Models.Vector.isInFrontOf(relativeBotPosVector, torpedoVector)) {
            return relativeBotPosVector
                    .isCloseToVector2(torpedoVector, bot.getSize() + torpedo.getSize() + sensitivity);
        } else {
            return false;
        }
    }

    public static int torpedoEscapeHeading(GameObject bot, GameObject torpedo) {
        Vector botPosVector = new Vector(bot.getPosition());
        Vector relativeBotPosVector = Vector.minus(botPosVector, new Vector(torpedo.getPosition()));
        Vector torpedoHeadingVector = new Vector(torpedo.getCurrentHeading());
        Vector projection = Vector.projection(relativeBotPosVector, torpedoHeadingVector);
        Vector escapeHeading = Vector.minus(relativeBotPosVector, projection);
        return escapeHeading.toHeading();
    }

    public static double distanceToTorpedoPath(GameObject bot, GameObject torpedo) {
        Vector botPosVector = new Vector(bot.getPosition());
        Vector relativeBotPosVector = Vector.minus(botPosVector, new Vector(torpedo.getPosition()));
        Vector torpedoVector = new Vector(torpedo.getCurrentHeading());
        Vector torpedoHeadingVector = new Vector(torpedo.getCurrentHeading());
        Vector projection = Vector.projection(relativeBotPosVector, torpedoHeadingVector);
        return Vector.length(projection);
    }
}

