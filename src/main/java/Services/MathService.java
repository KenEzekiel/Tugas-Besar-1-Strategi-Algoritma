package Services;

import Enums.ObjectTypes;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Models.Vector;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class MathService {
    public static double getDistanceBetween(GameObject object1, GameObject object2) {
        return getDistanceBetween(object1.getPosition(), object2.getPosition()) - object1.size - object2.size;
    }

    public static double getDistanceBetween(double x1, double y1, double x2, double y2) {
        var dx = Math.abs(x2 - x1);
        var dy = Math.abs(y2 - y1);
        return Math.sqrt(dx * dx + dy * dy);
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

    public static int calcObjectValueBetweenObjects(GameState state, Position obj1, Position obj2, int threshold) {
        Vector refVector = new Vector(obj1, obj2);
        return state.getGameObjects().stream()
                .filter(obj -> obj.getGameObjectType() == ObjectTypes.FOOD ||
                        obj.getGameObjectType() == ObjectTypes.GAS_CLOUD ||
                        obj.getGameObjectType() == ObjectTypes.ASTEROID_FIELD ||
                        obj.getGameObjectType() == ObjectTypes.SUPER_FOOD)
                .filter(obj -> new Vector(obj1, obj.getPosition()).isCloseToVector(refVector, threshold))
                .mapToInt(GameObject::getSize).sum();

    }
    public static boolean guaranteeHitTorpedo(Position firePos, GameObject targetPlayer) {
        int modifierConstant = 1;
        int torpedoSpeed = 10;
        double shipSize = (double) targetPlayer.getSize();
        double distance = getDistanceBetween(firePos, targetPlayer.getPosition());
        int size = targetPlayer.getSize();
        return (200 * distance < shipSize * shipSize * torpedoSpeed);
    }
}

