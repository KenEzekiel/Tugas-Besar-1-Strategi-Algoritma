package Models;

import Enums.ObjectTypes;
import Enums.PlayerEffects;

import java.util.List;
import java.util.UUID;

import static Models.Vector.*;

public class GameObject {
    public UUID id;
    public Integer size;
    public Integer speed;
    public Integer currentHeading;
    public Position position;
    public ObjectTypes gameObjectType;
    public PlayerEffects effects;
    public Integer torpedoSalvoCount;
    public Integer supernovaAvailable;
    public Integer teleporterCount;
    public Integer shieldCount;
    public boolean hasJustFireTeleport = false;

    public GameObject getFiredTeleport() {
        return firedTeleport;
    }

    public void setFiredTeleport(GameObject firedTeleport) {
        this.firedTeleport = firedTeleport;
    }

    public GameObject firedTeleport = null;

    public GameObject(UUID id, Integer size, Integer speed, Integer currentHeading, Position position, ObjectTypes gameObjectType, PlayerEffects effects, Integer torpedoSalvoCount, Integer supernovaAvailable, Integer teleporterCount, Integer shieldCount) {
        this.id = id;
        this.size = size;
        this.speed = speed;
        this.currentHeading = currentHeading;
        this.position = position;
        this.gameObjectType = gameObjectType;
        this.effects = effects;
        this.torpedoSalvoCount = torpedoSalvoCount;
        this.supernovaAvailable = supernovaAvailable;
        this.teleporterCount = teleporterCount;
        this.shieldCount = shieldCount;
    }

    public GameObject(UUID id, Integer size, Integer speed, Integer currentHeading, Position position, ObjectTypes gameObjectType) {
        this.id = id;
        this.size = size;
        this.speed = speed;
        this.currentHeading = currentHeading;
        this.position = position;
        this.gameObjectType = gameObjectType;
        this.effects = new PlayerEffects(0);
        this.torpedoSalvoCount = 0;
        this.supernovaAvailable = 0;
        this.teleporterCount = 0;
        this.shieldCount = 0;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ObjectTypes getGameObjectType() {
        return gameObjectType;
    }

    public void setGameObjectType(ObjectTypes gameObjectType) {
        this.gameObjectType = gameObjectType;
    }

    public Position getProjectedPosition() {
        return getProjectedPosition(1);
    }

    public Position getProjectedPosition(double tickCount) {
        var headingRad = Math.toRadians(this.currentHeading);
        var startPosition = this.position;
        int x = 0, y = 0;
        if (this.currentHeading <= 90) {
            x = startPosition.x + (int) Math.round(Math.round(speed * Math.cos(headingRad)) * tickCount);
            y = startPosition.y + (int) Math.round(Math.round(speed * Math.sin(headingRad)) * tickCount);
        } else if (this.currentHeading <= 180) {
            x = startPosition.x - (int) Math.round(Math.round(speed * Math.cos(Math.PI - headingRad)) * tickCount);
            y = startPosition.y + (int) Math.round(Math.round(speed * Math.sin(Math.PI - headingRad)) * tickCount);
        } else if (this.currentHeading <= 270) {
            x = startPosition.x - (int) Math.round(Math.round(speed * Math.cos(headingRad - Math.PI)) * tickCount);
            y = startPosition.y - (int) Math.round(Math.round(speed * Math.sin(headingRad - Math.PI)) * tickCount);

        } else if (this.currentHeading <= 360) {
            x = startPosition.x + (int) Math.round(Math.round(speed * Math.cos(2 * Math.PI - headingRad)) * tickCount);
            y = startPosition.y - (int) Math.round(Math.round(speed * Math.sin(2 * Math.PI - headingRad)) * tickCount);
        }

        return new Position(x, y);
    }

    public static GameObject FromStateList(UUID id, List<Integer> stateList) {
        Position position = new Position(stateList.get(4), stateList.get(5));
        if (stateList.size() < 11) {
            return new GameObject(id, stateList.get(0), stateList.get(1), stateList.get(2), position, ObjectTypes.valueOf((stateList.get((3)))));
        }
        return new GameObject(id, stateList.get(0), stateList.get(1), stateList.get(2), position, ObjectTypes.valueOf(stateList.get(3)),
                new PlayerEffects(stateList.get(6)), stateList.get(7), stateList.get(8), stateList.get(9), stateList.get(10));
    }

    public boolean isObjectCloseToVector(Position startPos, Position endPos, int threshold) {
        Vector refVector = new Vector(startPos, endPos);
        Vector objVector = new Vector(startPos, this.getPosition());
        Vector projVector = projection(objVector, refVector);
        double distance = length(minus(objVector, projVector));
        return (distance - this.getSize() <= threshold) && (isBetween(projVector, refVector));
    }
}


