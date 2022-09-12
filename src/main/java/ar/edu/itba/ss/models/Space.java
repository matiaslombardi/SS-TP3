package main.java.ar.edu.itba.ss.models;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Space {
    private final double height;
    private final double width;
    private final double slitSize;
    private final List<Particle> particleList;
    private final Queue<Collision> collisions;

    public Space(double height, double width, double slitSize, List<Particle> particles) {
        this.height = height;
        this.width = width;
        this.slitSize = slitSize;
        this.particleList = particles;
        this.collisions = new PriorityQueue<>();
    }

    public void evolveState() {
        // TODO: la proxima vez que buscamos colisiones, podriamos buscar solo con las que chocaron
        // TODO: de las que chocan, actualizar su posicion
        Collision firstCollision = collisions.remove();
        // get ids y si coincide en forEach, actualizar los angulos y velocidades
        particleList.forEach(particle -> {
            particle.updatePosition(firstCollision.getTc());
        });

        // TODO: cambiar a mapa
        long indexA = firstCollision.getIndexA();
        long indexB = firstCollision.getIndexB();

        Particle particle = particleList.stream().filter(p -> p.getId() == indexA).findFirst().orElseThrow(RuntimeException::new);

        try {
            Walls wall = Walls.values()[(int) indexB];
            particle.collideWithWall(wall);
            //particle.updatePosition();
        } catch (IndexOutOfBoundsException e) {
            Particle other = particleList.stream().filter(p -> p.getId() == indexB).findFirst()
                    .orElseThrow(RuntimeException::new);

            double[] deltaR = getDeltaR(particle, other);
            double[] deltaV = getDeltaV(particle, other);

            double sigma = getSigma(particle, other);
            particle.collideWithParticle(other, dotProduct(deltaV, deltaR), sigma);
        }
    }

    public void computeCollisions() {
        wallCollisions();
        particleCollisions();
    }


    private void wallCollisions() { // TODO: como representamos slit?
        particleList.forEach(particle -> {
            double vx = particle.getSpeedX();
            double vy = particle.getSpeedY();

            double tc;

            if (vx < 0) {
                tc = (0 + particle.getRadius() - particle.getPosition().getX()) / vx;
                collisions.add(new Collision(particle.getId(), Walls.LEFT.ordinal(), tc));
            } else if (vx > 0) {
                tc = (width - particle.getRadius() - particle.getPosition().getX()) / vx;
                collisions.add(new Collision(particle.getId(), Walls.RIGHT.ordinal(), tc));
            }

            if (vy < 0) {
                tc = (0 + particle.getRadius() - particle.getPosition().getY()) / vy;
                collisions.add(new Collision(particle.getId(), Walls.DOWN.ordinal(), tc));
            } else if (vy > 0) {
                tc = (height - particle.getRadius() - particle.getPosition().getY()) / vy;
                collisions.add(new Collision(particle.getId(), Walls.UP.ordinal(), tc));
            }
        });
    }

    private void particleCollisions() { // TODO: fijarse de no poner 2 veces las colisiones
        particleList.forEach(particleI -> particleList.forEach(particleJ -> {
            if (!particleI.equals(particleJ)) {
                double sigma = getSigma(particleJ, particleI);

                double[] deltaR = getDeltaR(particleJ, particleI);
                double[] deltaV = getDeltaV(particleJ, particleI);

                if (Double.compare(dotProduct(deltaV, deltaR), 0) < 0) {
                    double d = calculate(deltaV, deltaR, sigma);
                    if (Double.compare(d, 0) >= 0) {
                        double tc = -(dotProduct(deltaV, deltaR) + Math.sqrt(d)) / dotProduct(deltaV, deltaV);
                        collisions.add(new Collision(particleI.getId(), particleJ.getId(), tc));
                    }
                }
            }
        }));
    }

    private double getSigma(Particle particleJ, Particle particleI) {
        return particleI.getRadius() + particleJ.getRadius();

    }

    private double[] getDeltaR(Particle particleJ, Particle particleI) {
        return new double[]{particleJ.getPosition().getX() - particleI.getPosition().getX(),
                particleJ.getPosition().getY() - particleI.getPosition().getY()};
    }

    private double[] getDeltaV(Particle particleJ, Particle particleI) {
        return new double[]{particleJ.getSpeedX() - particleI.getSpeedX(),
                particleJ.getSpeedY() - particleI.getSpeedY()};
    }

    private double dotProduct(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) throw new RuntimeException();

        double toRet = 0;
        for (int i = 0; i < vectorA.length; i++) {
            toRet += vectorA[i] * vectorB[i];
        }
        return toRet;
    }

    private double calculate(double[] deltaV, double[] deltaR, double sigma) {
        return Math.pow(dotProduct(deltaV, deltaR), 2) - dotProduct(deltaV, deltaV) * (dotProduct(deltaR, deltaR) - Math.pow(sigma, 2));
    }

    public List<Particle> getParticleList() {
        return particleList;
    }
}