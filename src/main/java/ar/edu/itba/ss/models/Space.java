package main.java.ar.edu.itba.ss.models;

import java.util.*;
import java.util.stream.Collectors;

public class Space {
    private final double height;
    private final double width;
    private final double slitSize;
    private final Map<Long, Particle> particleMap;
    private final Queue<Collision> collisions;
    private final Map<Long, Collision> collisionIndexes;

    public Space(double height, double width, double slitSize, List<Particle> particles) {
        this.height = height;
        this.width = width;
        this.slitSize = slitSize;
        this.particleMap = particles.stream().collect(Collectors
                .toMap(Particle::getId, particle -> particle));
        this.collisions = new PriorityQueue<>();
        collisionIndexes = new HashMap<>();
    }

    public void evolveState() {
        // TODO: la proxima vez que buscamos colisiones, podriamos buscar solo con las que chocaron
        // TODO: de las que chocan, actualizar su posicion
        Collision firstCollision = collisions.remove();
        while (firstCollision.isInvalid()) {
            firstCollision = collisions.remove();
            if (firstCollision == null)
                return;
        }
        // get ids y si coincide en forEach, actualizar los angulos y velocidades
        Collision finalFirstCollision = firstCollision;
        for (Particle particle : particleMap.values()) {
            particle.updatePosition(finalFirstCollision.getTc());
        }

        // TODO: cambiar a mapa
        long indexA = firstCollision.getIndexA();
        long indexB = firstCollision.getIndexB();


        Particle particle = particleMap.get(indexA);

        try {
            Walls wall = Walls.values()[(int) indexB];
            particle.collideWithWall(wall);
        } catch (IndexOutOfBoundsException e) {
            Particle other = particleMap.get(indexB);

            double[] deltaR = getDeltaR(particle, other);
            double[] deltaV = getDeltaV(particle, other);

            double sigma = getSigma(particle, other);
            particle.collideWithParticle(other, dotProduct(deltaV, deltaR), sigma);
        }

        collisionIndexes.remove(firstCollision.getIndexA());
        collisionIndexes.remove(firstCollision.getIndexB());
        for (Collision collision : collisions) {
            collision.updateTc(firstCollision.getTc());
        }
    }

    public void computeCollisions() {
        wallCollisions();
        particleCollisions();
    }


    private void wallCollisions() { // TODO: como representamos slit?
        particleMap.values().forEach(particle -> {
            double vx = particle.getSpeedX();
            double vy = particle.getSpeedY();

            double tc;

            Collision collision = null;
            if (vx < 0) {
                tc = (0 + particle.getRadius() - particle.getPosition().getX()) / vx;
                collision = new Collision(particle.getId(), Walls.LEFT.ordinal(), tc);
            } else if (vx > 0) {
                tc = (width - particle.getRadius() - particle.getPosition().getX()) / vx;
                collision = new Collision(particle.getId(), Walls.RIGHT.ordinal(), tc);
            }

            Collision horizontalCollision = collision;

            if (vy < 0) {
                tc = (0 + particle.getRadius() - particle.getPosition().getY()) / vy;
                // TODO: segunda iter
                collision = new Collision(particle.getId(), Walls.DOWN.ordinal(), tc);
            } else if (vy > 0) {
                tc = (height - particle.getRadius() - particle.getPosition().getY()) / vy;
                collision = new Collision(particle.getId(), Walls.UP.ordinal(), tc);
            }

            if (horizontalCollision != null && Double.compare(collision.getTc(), horizontalCollision.getTc()) > 0)
                collision = horizontalCollision;

            collisionIndexes.put(particle.getId(), collision);
            collisions.add(collision);
        });
    }

    // TODO: SOLO AGREGAR EL MINIMO CUANDO ACTUALIZAMOS
    private void particleCollisions() {
        List<Particle> particles = new ArrayList<>(particleMap.values());
        for (int i = 0; i < particles.size(); i++) {
            Particle particleI = particles.get(i);

            double particleITc = Optional.ofNullable(collisionIndexes.get(particleI.getId()))
                    .map(Collision::getTc).orElse(Double.MAX_VALUE);

            double minTc = Double.MAX_VALUE;
            Particle toCollide = null;

            for (int j = i + 1; j < particles.size() - 1; j++) {
                Particle particleJ = particles.get(j);
                double sigma = getSigma(particleJ, particleI);

                double[] deltaR = getDeltaR(particleJ, particleI);
                double[] deltaV = getDeltaV(particleJ, particleI);

                if (Double.compare(dotProduct(deltaV, deltaR), 0) < 0) {
                    double d = calculate(deltaV, deltaR, sigma);
                    if (Double.compare(d, 0) >= 0) {
                        double tc = -(dotProduct(deltaV, deltaR) + Math.sqrt(d)) / dotProduct(deltaV, deltaV);
                        double particleJTc = Optional.ofNullable(collisionIndexes.get(particleJ.getId()))
                                .map(Collision::getTc).orElse(Double.MAX_VALUE);

                        if (Double.compare(minTc, tc) > 0
                                && Double.compare(particleITc, tc) > 0
                                && Double.compare(particleJTc, tc) > 0) {
                            minTc = tc;
                            toCollide = particleJ;
                        }
                    }
                }
            }
            if (toCollide != null) {
                Collision collision = new Collision(particleI.getId(), toCollide.getId(), minTc);
                collisions.add(collision);
                // TODO: sacar si hay otra colision
                if (collisionIndexes.containsKey(particleI.getId())) {
                    collisionIndexes.get(particleI.getId()).setInvalid(true);
                }

                if (collisionIndexes.containsKey(toCollide.getId())) {
                    collisionIndexes.get(toCollide.getId()).setInvalid(true);
                }

                collisionIndexes.put(particleI.getId(), collision);
                collisionIndexes.put(toCollide.getId(), collision);
            }
        }
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

    public Map<Long, Particle> getParticleMap() {
        return particleMap;
    }
}