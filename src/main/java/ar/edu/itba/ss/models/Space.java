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
        Collision firstCollision = collisions.poll();
//        while (firstCollision.isInvalid()) {
//            firstCollision = collisions.poll();
        if (firstCollision == null)
            return; // TODO capaz terminar la ejecucion con thr
//        }

        lastCollision = firstCollision;
        // get ids y si coincide en forEach, actualizar los angulos y velocidades
        for (Particle particle : particleMap.values())
            particle.updatePosition(firstCollision.getTc());

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
        for (Collision collision : collisions)
            collision.updateTc(firstCollision.getTc());
    }

    public void computeCollisions() {
        List<Particle> toAnalyze;
        if (firstIter) {
            toAnalyze = new ArrayList<>(particleMap.values());
            firstIter = false;
        } else {
            toAnalyze = new ArrayList<>();
            toAnalyze.add(particleMap.get(lastCollision.getIndexA()));

            long indexB = lastCollision.getIndexB();
            if (indexB >= Walls.values().length)
                toAnalyze.add(particleMap.get(indexB));
        }

        analyzeCollision(toAnalyze);
        evolveState();
    }

    private void wallCollisions() { // TODO: como representamos slit?
        particleMap.values().forEach(this::wallCollision);
    }

    private Collision wallCollision(Particle particle) {
        double minTc = Double.MAX_VALUE;
        int ordinal = Walls.LEFT.ordinal();

        for (Walls wall : Walls.values()) {
            double tc = wall.getCollisionTime(particle);
            if (Double.compare(tc, minTc) < 0) {
                minTc = tc;
                ordinal = wall.ordinal();
            }
        }

//        if (collisionIndexes.containsKey(particle.getId())) {
//            if (Double.compare(minTc, collisionIndexes.get(particle.getId()).getTc()) > 0)
//                return null; // si va a chocar con una particula antes no hagas nada
//
////            collisionIndexes.get(particle.getId()).setInvalid(true);
//            collisions.remove(collisionIndexes.get(particle.getId()));
//        }

        Collision collision = new Collision(particle.getId(), ordinal, minTc);
        //collisionIndexes.put(particle.getId(), collision);
        //collisions.add(collision);
        return collision;
    }

//    private void particleCollisions() {
//        List<Particle> particles = new ArrayList<>(particleMap.values());
//        for (int i = 0; i < particles.size() - 1; i++) {
//            Particle particleI = particles.get(i);
//
//            double particleITc = Optional.ofNullable(collisionIndexes.get(particleI.getId()))
//                    .map(Collision::getTc).orElse(Double.MAX_VALUE);
//
//            double minTc = Double.MAX_VALUE;
//            Particle toCollide = null;
//
//            for (int j = i + 1; j < particles.size(); j++) {
//                Particle particleJ = particles.get(j);
//                double sigma = getSigma(particleJ, particleI);
//
//                double[] deltaR = getDeltaR(particleJ, particleI);
//                double[] deltaV = getDeltaV(particleJ, particleI);
//
//                if (Double.compare(dotProduct(deltaV, deltaR), 0) < 0) {
//                    double d = calculate(deltaV, deltaR, sigma);
//                    if (Double.compare(d, 0) >= 0) {
//                        double tc = -(dotProduct(deltaV, deltaR) + Math.sqrt(d)) / dotProduct(deltaV, deltaV);
//                        double particleJTc = Optional.ofNullable(collisionIndexes.get(particleJ.getId()))
//                                .map(Collision::getTc).orElse(Double.MAX_VALUE);
//
//                        if (Double.compare(minTc, tc) > 0
//                                && Double.compare(particleITc, tc) > 0
//                                && Double.compare(particleJTc, tc) > 0) {
//                            minTc = tc;
//                            toCollide = particleJ;
//                        }
//                    }
//                }
//            }
//            if (toCollide != null) {
//
//                if (collisionIndexes.containsKey(toCollide.getId())) {
//                    collisions.remove(collisionIndexes.get(toCollide.getId()));
//                    //collisionIndexes.get(toCollide.getId()).setInvalid(true);
//                }
//
//                Collision collision = new Collision(particleI.getId(), toCollide.getId(), minTc);
//                collisions.add(collision);
//
//                collisionIndexes.put(particleI.getId(), collision);
//                collisionIndexes.put(toCollide.getId(), collision);
//            }
//        }
//    }

    private void analyzeCollision(List<Particle> toAnalyze) {
        toAnalyze.forEach(particleI -> {
            Collision possibleCollision = wallCollision(particleI);

            double particleITc = Optional.ofNullable(collisionIndexes.get(particleI.getId()))
                    .map(Collision::getTc).orElse(Double.MAX_VALUE);

            double minTc = possibleCollision.getTc();
            long toCollide = possibleCollision.getIndexB();

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
                            System.out.println(tc);
                        }
                        double particleJTc = Optional.ofNullable(collisionIndexes.get(particleJ.getId()))
                                .map(Collision::getTc).orElse(Double.MAX_VALUE);

                        if (Double.compare(minTc, tc) > 0
                                && Double.compare(particleITc, tc) > 0
                                && Double.compare(particleJTc, tc) > 0) {
                            minTc = tc;
                            toCollide = particleJ.getId();
                        }
                    }
                }
            }
//
//            if (toCollide != null) {
            if (collisionIndexes.containsKey(particleI.getId())) {
                System.out.println("AAAAAAA");
                collisions.remove(collisionIndexes.get(particleI.getId()));
            }
//                    collisionIndexes.get(particleI.getId()).setInvalid(true);

            if (collisionIndexes.containsKey(toCollide))
                collisions.remove(collisionIndexes.get(toCollide));

//                collisionIndexes.get(toCollide.getId()).setInvalid(true);

            Collision collision = new Collision(particleI.getId(), toCollide, minTc);

            collisionIndexes.put(particleI.getId(), collision);
            if (toCollide >= Walls.values().length)
                collisionIndexes.put(toCollide, collision);

            collisions.add(collision);
//            }
        });

    }

    private double getSigma(Particle particleJ, Particle particleI) {
        return particleI.getRadius() + particleJ.getRadius();
    }

    private double[] getDeltaR(Particle particleJ, Particle particleI) {
        return new double[]{
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