package main.java.ar.edu.itba.ss.models;

public class Point {
    protected double x;
    protected double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
        this.x = this.x + dx;
        this.y = this.y + dy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
