package Services;

import Models.GameObject;

public class ObjectDistanceDto {
    public GameObject object;
    public double distance;


    public ObjectDistanceDto(GameObject object, double distance) {
        this.object = object;
        this.distance = distance;
    }
}
