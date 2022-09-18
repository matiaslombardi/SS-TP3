package main.java.ar.edu.itba.ss.models;

import java.util.Objects;

public class Particle {
    private static long SEQ = Walls.values().length;//0;
    private Point position;
    private final long id;
    private double speedX;
    private double speedY;
    private final double mass;
    private final double radius;
    private final boolean isStatic;

    public Particle(double speed, double mass, double radius) {
        this(speed, mass, radius, false);
    }

    public Particle(double speed, double mass, double radius, boolean isStatic) {
        this.id = SEQ++;
        double direction = Math.random() * 2 * Math.PI;
        this.speedX = speed * Math.cos(direction);
        this.speedY = speed * Math.sin(direction);
        this.mass = mass;
        this.radius = radius;
        this.isStatic = isStatic;
    }

    public void updatePosition(double tc) {
        double dx = speedX * tc;
        double dy = speedY * tc;
        this.position.move(dx, dy);
    }

    public void collideWithWall(Walls wall) {
        switch (wall) {
            case UP, DOWN -> speedY *= -1;
            case LEFT, RIGHT, SLIT_TOP, SLIT_BOTTOM -> speedX *= -1;
        }
    }

    public void collideWithParticle(Particle other, double delta, double sigma) {

        double j = (2 * mass * other.getMass() * delta) / (sigma * (mass + other.getMass()));
        double jx = (j * (position.getX() - other.getPosition().getX())) / sigma;
        double jy = (j * (position.getY() - other.getPosition().getY())) / sigma;

        speedX -= jx / mass;
        speedY -= jy / mass;

        if (!other.isStatic) {
            other.speedX += jx / other.getMass();
            other.speedY += jy / other.getMass();
        }

//        System.out.println("Distance " + Math.sqrt(Math.pow(position.getX() - other.getPosition().getX(), 2) + Math.pow(position.getY() - other.getPosition().getY(), 2)));
//
//        System.out.println("Speed " + Math.sqrt(other.speedX * other.speedX + other.speedY * other.speedY));

    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Particle{" + "id=" + id + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return getId() == particle.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isStatic() {
        return isStatic;
    }
}
