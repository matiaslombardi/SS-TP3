package main.java.ar.edu.itba.ss.models;

public enum Walls {
    LEFT(0, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedX() < 0)
                return (getX() + p.getRadius() - p.getPosition().getX()) / p.getSpeedX();

            return Double.MAX_VALUE;
        }
    },
    RIGHT(Walls.WIDTH, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedX() > 0) {
                return (getX() - p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
            }
            return Double.MAX_VALUE;
        }
    },
    UP(0, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedY() < 0)
                return (getY() + p.getRadius() - p.getPosition().getY()) / p.getSpeedY();

            return Double.MAX_VALUE;
        }
    },
    DOWN(0, Walls.HEIGHT) {
        @Override
        public double getCollisionTime(Particle p) {
            if (p.getSpeedY() > 0)
                return (getY() - p.getRadius() - p.getPosition().getY()) / p.getSpeedY();

            return Double.MAX_VALUE;
        }
    },

    SLIT_TOP(Walls.WIDTH / 2, 0) {
        @Override
        public double getCollisionTime(Particle p) {
            double height = (Walls.HEIGHT - Walls.SLIT_HEIGHT) / 2;

            if (p.getPosition().getX() > getX()) { // Lado derecho
                if (p.getSpeedX() > 0)
                    return Double.MAX_VALUE;

                double tc = (getX() + p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;

                if (yTc + p.getRadius() > height)
                    return Double.MAX_VALUE;

                return tc;
            } else if (p.getPosition().getX() < getX()) { // Lado izquierdo
                if (p.getSpeedX() < 0)
                    return Double.MAX_VALUE;

                double tc = (getX() - p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;
                if (yTc + p.getRadius() > height)
                    return Double.MAX_VALUE;

                return tc;
            } else { // Centro
                // TODO: ver que hacemo
                return Double.MAX_VALUE;
            }
        }
    },

    //TODO: checkear si height 0 es arriba
    SLIT_BOTTOM(Walls.WIDTH / 2, (Walls.HEIGHT + Walls.SLIT_HEIGHT) / 2) {
        @Override
        public double getCollisionTime(Particle p) {

            if (p.getPosition().getX() > getX()) { // Lado derecho
                if (p.getSpeedX() > 0)
                    return Double.MAX_VALUE;

                double tc = (getX() + p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;

                if (yTc - p.getRadius() < getY())
                    return Double.MAX_VALUE;

                return tc;
            } else if (p.getPosition().getX() < getX()) { // Lado izquierdo
                if (p.getSpeedX() < 0)
                    return Double.MAX_VALUE;

                double tc = (getX() - p.getRadius() - p.getPosition().getX()) / p.getSpeedX();
                double yTc = p.getPosition().getY() + p.getSpeedY() * tc;
                if (yTc - p.getRadius() < getY())
                    return Double.MAX_VALUE;

                return tc;
            } else { // Centro
                // TODO: ver que hacemo
                return Double.MAX_VALUE;
            }
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public abstract double getCollisionTime(Particle p);
}