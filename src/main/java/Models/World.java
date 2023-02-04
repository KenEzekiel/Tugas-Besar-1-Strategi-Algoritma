package Models;

public class World {

    public Position centerPoint;
    public int radius;
    public int currentTick;

    public Position getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Position centerPoint) {
        this.centerPoint = centerPoint;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }
}
