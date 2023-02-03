package Models;

public class ActionWeight {
    private int heading;
    private double weight;


    public ActionWeight(int heading, double weight) {
        this.heading = heading;
        this.weight = weight;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
