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

    public static int calcObjectValueBetweenObjects(List<GameObject> objects, Position pos1, Position pos2, int threshold) {
        Vector refVector = new Vector(pos1, pos2);
        return objects.stream()
                .filter(obj -> obj.getGameObjectType() == ObjectTypes.FOOD ||
                        obj.getGameObjectType() == ObjectTypes.GAS_CLOUD ||
                        obj.getGameObjectType() == ObjectTypes.ASTEROID_FIELD ||
                        obj.getGameObjectType() == ObjectTypes.SUPER_FOOD)
                .filter(obj -> new Vector(pos1, obj.getPosition()).isCloseToVector(refVector, threshold))
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
}

