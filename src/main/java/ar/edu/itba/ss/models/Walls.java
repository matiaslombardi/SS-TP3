package main.java.ar.edu.itba.ss.models;

import main.java.ar.edu.itba.ss.utils.Configuration;

public enum Walls {
    LEFT(0, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedX() < 0)
                return (0 + p.getRadius() - p.getPosition().getX()) / p.getSpeedX();

            return Double.MAX_VALUE;
        }
    },
    RIGHT(Walls.WIDTH, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedX() > 0)
                return (Configuration.width - p.getRadius() - p.getPosition().getX()) / p.getSpeedX();

            return Double.MAX_VALUE;
        }
    },
    DOWN(0, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedY() < 0)
                return (0 + p.getRadius() - p.getPosition().getY()) / p.getSpeedY();

            return Double.MAX_VALUE;
        }
    },
    UP(0, Walls.HEIGHT) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedY() > 0)
                return (Configuration.height - p.getRadius() - p.getPosition().getY()) / p.getSpeedY();

            return Double.MAX_VALUE;
        }
    },

    SLIT_TOP(Walls.WIDTH / 2, Walls.HEIGHT) {
        @Override
        public double getCollisionTime(Particle p) {
            double height = (Configuration.height - Configuration.slitHeight) / 2;
            double x = Configuration.width / 2;
            double y = Configuration.height;

            if (Double.compare(p.getPosition().getX(), x) > 0) { // Lado derecho
                if (p.getSpeedX() > 0)
                    return Double.MAX_VALUE;

                double tc = (x + p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;

                // y + radio - HEIGHT / 2 + SLIT / 2
                if (Double.compare(yTc + p.getRadius(), y - height) <= 0)
                    return Double.MAX_VALUE;

                return tc;
            } else if (Double.compare(p.getPosition().getX(), x) < 0) { // Lado izquierdo
                if (p.getSpeedX() < 0)
                    return Double.MAX_VALUE;

                double tc = (x - p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;
                if (Double.compare(yTc + p.getRadius(), y - height) <= 0)
                    return Double.MAX_VALUE;
                return tc;
            }// Centro

            return Double.MAX_VALUE;
        }
    },
    SLIT_BOTTOM(Walls.WIDTH / 2, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            double height = (Walls.HEIGHT - Walls.SLIT_HEIGHT) / 2;
            double x = Walls.WIDTH / 2;
            double y = 0;

            if (p.getPosition().getX() > x) { // Lado derecho
                if (p.getSpeedX() > 0)
                    return Double.MAX_VALUE;

                double tc = (x + p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;

                // y - radio - (HEIGHT / 2 - SLIT / 2)
                if (Double.compare(yTc - p.getRadius(), height) >= 0)
                    return Double.MAX_VALUE;

                return tc;
            } else if (p.getPosition().getX() < x) { // Lado izquierdo
                if (p.getSpeedX() < 0)
                    return Double.MAX_VALUE;

                double tc = (x - p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;
                if (Double.compare(yTc - p.getRadius(), height) >= 0)
                    return Double.MAX_VALUE;

                return tc;
            }

            return Double.MAX_VALUE;
        }
    };

    public final static double HEIGHT = 0.09;
    public final static double WIDTH = 0.24;
    public final static double SLIT_HEIGHT = 0.01;
    private final double x;
    private final double y;


    Walls(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract double getCollisionTime(Particle p);
}
