package main.java.ar.edu.itba.ss.models;

import javax.swing.text.Position;
import java.util.Objects;

public class Particle {
    private static long SEQ = Walls.values().length;//0;
    private Point position;
    private final long id;
    private double speedX;
    private double speedY;
    private final double mass;
    private final double radius;

    public Particle(double speed, double mass, double radius) {
        this.id = SEQ++;
        double direction = Math.random() * 2 * Math.PI;
        this.speedX = speed * Math.cos(direction);
        this.speedY = speed * Math.sin(direction);
        this.mass = mass;
        this.radius = radius;
    }

    public void updatePosition(double tc) {
        double dx = speedX * tc;
        double dy = speedY * tc;
        this.position.move(dx, dy);
    }

    public void collideWithWall(Walls wall) {
        switch (wall) {
            case UP:
            case DOWN:
                speedY *= -1;
                break;
            case LEFT:
            case RIGHT:
            case SLIT_TOP:
            case SLIT_BOTTOM:
                speedX *= -1;
                break;
            case SLIT_BORDER:
                
                Particle slitParticle = new Particle(0, mass*1000, 0);
                double h = (Walls.HEIGHT - Walls.SLIT_HEIGHT) / 2;
                Point pos = new Point(Walls.WIDTH / 2, h);
                if (getPosition().getY() > Walls.HEIGHT / 2) {
                    pos.setY(Walls.HEIGHT - h);
                }
                slitParticle.setPosition(pos);

                double[] deltaR = Space.getDeltaR(this, slitParticle);
                double[] deltaV = Space.getDeltaV(this, slitParticle);

                double sigma = Space.getSigma(this, slitParticle);
                collideWithParticle(slitParticle, Space.dotProduct(deltaV, deltaR), sigma);
                break;

        }
    }

    public void collideWithParticle(Particle other, double delta, double sigma) {
//        if (other.isStatic) {
//            double angle = Math.atan2(-getSpeedY(), -speedX);  // - because angle is from obstacle towards particle
//            double angleCos = Math.cos(angle);
//            double angleSen = Math.sin(angle);
//            double[][] collisionOp = {
//                    {-angleCos*angleCos + angleSen*angleSen, -2*angleSen*angleCos},
//                    {-2*angleSen*angleCos, -angleSen*angleSen + angleCos*angleCos}
//            };
//
//            speedX = collisionOp[0][0] * getSpeedX() + collisionOp[0][1] * getSpeedY();
//            speedY = collisionOp[1][0] * getSpeedX() + collisionOp[1][1] * getSpeedY();
//            return;
//        }
        double j = (2 * mass * other.getMass() * delta) / (sigma * (mass + other.getMass()));
        double jx = (j * (position.getX() - other.getPosition().getX())) / sigma;
        double jy = (j * (position.getY() - other.getPosition().getY())) / sigma;

        speedX -= jx / mass;
        speedY -= jy / mass;

<<<<<<< HEAD

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

<<<<<<< HEAD
}
=======
    public boolean isStatic() {
        return isStatic;
    }
}
>>>>>>> Merge
