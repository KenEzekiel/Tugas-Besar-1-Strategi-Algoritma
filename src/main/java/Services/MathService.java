package Services;

import Models.GameObject;
import Models.Position;

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
        return (int) (v * (180 / Math.PI));
    }

    public static int reverseHeading(int Heading) {
        return (int) ((Heading + 180) % 360);
    }
}
