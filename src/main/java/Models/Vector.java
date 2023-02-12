package Models;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Vector {
    private double x;
    private double y;

    public Vector() {
        this.x = 0;
        this.y = 0;
    }
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Vector(Position end) {
        this.x = end.getX();
        this.y = end.getY();
    }
    public Vector(Position start, Position end) {
        this.x = end.getX() - start.getX();
        this.y = end.getY() - start.getY();
    }
    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public static Vector minus(Vector a, Vector b) {
        return new Vector(a.getX() - b.getX(), a.getY()-b.getY());
    }
    public static double dot(Vector a, Vector b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }
    public static Vector scalar(Vector v, double c) {
        return new Vector(v.getX() * c, v.getY() * c);
    }
    public static double length(Vector v) {
        return Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
    }
    public static Vector projection(Vector u, Vector v) {
        double result = dot(u, v) / dot(v, v);
        return scalar(v, result);
    }
    public boolean isBetween(Vector testVector, Vector reference) {
        boolean betweenX = ( min(0, reference.getX()) <= testVector.getX() ) && (testVector.getX() <= max(0, reference.getX()));
        boolean betweenY = ( min(0, reference.getY()) <= testVector.getY() ) && (testVector.getY() <= max(0, reference.getY()));
        return betweenX && betweenY;
    }
    public static double distanceToVector(Position p, Vector refVector) {
        Vector testVector = new Vector(p);
        Vector projVector = projection(testVector, refVector);
        return length(minus(testVector, projVector));
    }
    public boolean isCloseToVector(Vector refVector, int threshold) {
        Vector projVector = projection(this, refVector);
        double distance = length(minus(this, projVector));
        return (distance <= threshold) && (isBetween(projVector, refVector));
    }
    public static Vector degreeToUnit(int degree) {
        double radian = Math.toRadians(degree);
        double x = Math.cos(radian);
        double y = Math.sin(radian);
        return new Vector(x, y);
    }
}



