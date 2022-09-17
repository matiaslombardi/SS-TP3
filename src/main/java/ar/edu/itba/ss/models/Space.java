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


    private boolean firstIter;
    private Collision lastCollision;

    public Space(double height, double width, double slitSize, List<Particle> particles) {
        this.height = height;
        this.width = width;
        this.slitSize = slitSize;
        this.particleMap = particles.stream().collect(Collectors
                .toMap(Particle::getId, particle -> particle));
        this.collisions = new PriorityQueue<>();
        collisionIndexes = new HashMap<>();
        firstIter = true;
        lastCollision = null;
    }

    private void evolveState() {
        Collision firstCollision = collisions.remove();
        while (firstCollision.isInvalid()) {
            firstCollision = collisions.remove();
            if (firstCollision == null)
                return; // TODO capaz terminar la ejecucion con thr
        }
        lastCollision = firstCollision;

        System.out.println("Collision: " + firstCollision.getTc());
        // get ids y si coincide en forEach, actualizar los angulos y velocidades
        Collision finalFirstCollision = firstCollision;
        for (Particle particle : particleMap.values()) {
            particle.updatePosition(finalFirstCollision.getTc());
        }

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
        if (firstIter) {
            wallCollisions();
            particleCollisions();
            firstIter = false;
        } else {
            analyzeCollision();
        }
        evolveState();
    }

    private void wallCollisions() { // TODO: como representamos slit?
        particleMap.values().forEach(this::wallCollision);
    }

    private void wallCollision(Particle particle) {
        double minTc = Double.MAX_VALUE;
        int ordinal = Walls.LEFT.ordinal();

        for(Walls wall: Walls.values()){
            double tc = wall.getCollisionTime(particle);
            if(Double.compare(tc, minTc) < 0) {
                minTc = tc;
                ordinal = wall.ordinal();
            }
        }
        Collision collision = new Collision(particle.getId(), ordinal, minTc);

        if (collisionIndexes.containsKey(particle.getId()) &&
                Double.compare(collision.getTc(), collisionIndexes.get(particle.getId()).getTc()) > 0) {
            collisionIndexes.get(particle.getId()).setInvalid(true);
        }

        collisionIndexes.put(particle.getId(), collision);
        collisions.add(collision);
    }

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


    private void analyzeCollision() {
        List<Particle> particlesToCheck = new ArrayList<>();
        particlesToCheck.add(particleMap.get(lastCollision.getIndexA()));

        long indexB = lastCollision.getIndexB();
        if (indexB >= Walls.values().length) {
            particlesToCheck.add(particleMap.get(indexB));
        }

        particlesToCheck.forEach(particleI -> {
            wallCollision(particleI);

            double particleITc = Optional.ofNullable(collisionIndexes.get(particleI.getId()))
                    .map(Collision::getTc).orElse(Double.MAX_VALUE);

            double minTc = Double.MAX_VALUE;
            Particle toCollide = null;

            for (Particle particleJ : particleMap.values()) {
                if (particleI.getId() == particleJ.getId())
                    continue;

                double sigma = getSigma(particleJ, particleI);

                double[] deltaR = getDeltaR(particleJ, particleI);
                double[] deltaV = getDeltaV(particleJ, particleI);

                if (Double.compare(dotProduct(deltaV, deltaR), 0) < 0) {
                    double d = calculate(deltaV, deltaR, sigma);
                    if (Double.compare(d, 0) >= 0) {
                        double tc = -(dotProduct(deltaV, deltaR) + Math.sqrt(d)) / dotProduct(deltaV, deltaV);
                        if (tc < 0) {
                            System.out.println("aca");
                        }
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
                if (collisionIndexes.containsKey(particleI.getId())) {
                    collisionIndexes.get(particleI.getId()).setInvalid(true);
                }

                if (collisionIndexes.containsKey(toCollide.getId())) {
                    collisionIndexes.get(toCollide.getId()).setInvalid(true);
                }

                collisionIndexes.put(particleI.getId(), collision);
                collisionIndexes.put(toCollide.getId(), collision);
            }
        });

    }

    private double getSigma(Particle particleJ, Particle particleI) {
        return particleI.getRadius() + particleJ.getRadius();
    }

    private double[] getDeltaR(Particle particleJ, Particle particleI) {
        return new double[] {
                particleJ.getPosition().getX() - particleI.getPosition().getX(),
                particleJ.getPosition().getY() - particleI.getPosition().getY()
        };
    }

    private double[] getDeltaV(Particle particleJ, Particle particleI) {
        return new double[]{
                particleJ.getSpeedX() - particleI.getSpeedX(),
                particleJ.getSpeedY() - particleI.getSpeedY()
        };
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