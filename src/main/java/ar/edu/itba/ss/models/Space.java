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
//    private Collision lastCollision;
    private final List<Particle> particlesToAnalyze = new ArrayList<>();

    public Space(double height, double width, double slitSize, List<Particle> particles) {
        this.height = height;
        this.width = width;
        this.slitSize = slitSize;
        this.particleMap = particles.stream().collect(Collectors
                .toMap(Particle::getId, particle -> particle));
        this.collisions = new PriorityQueue<>();
        collisionIndexes = new HashMap<>();
        firstIter = true;
//        lastCollision = null;
    }

    private double evolveState() {
        Collision firstCollision = collisions.poll();
        if (firstCollision == null)
            throw new IllegalStateException("No collisions");

//        lastCollision = firstCollision;
        // get ids y si coincide en forEach, actualizar los angulos y velocidades
        double fp;
        long leftSide = 0;
        for (Particle particle : particleMap.values()) {
            particle.updatePosition(firstCollision.getTc());
            if (particle.getPosition().getX() < 0.12) //
                leftSide++;
        }
        fp = (double) leftSide / particleMap.size();

        long indexA = firstCollision.getIndexA();
        long indexB = firstCollision.getIndexB();
        Particle particle = particleMap.get(indexA);

        particlesToAnalyze.clear();

        particlesToAnalyze.add(particleMap.get(firstCollision.getIndexA()));
        collisionIndexes.remove(firstCollision.getIndexA());

        if (indexB < Walls.values().length){
            Walls wall = Walls.values()[(int) indexB];
            particle.collideWithWall(wall);
        } else {
            Particle other = particleMap.get(indexB);

            particlesToAnalyze.add(other);
            collisionIndexes.remove(firstCollision.getIndexB());

            double[] deltaR = getDeltaR(particle, other);
            double[] deltaV = getDeltaV(particle, other);

            double sigma = getSigma(particle, other);
            particle.collideWithParticle(other, dotProduct(deltaV, deltaR), sigma);
        }

        for (Collision collision : collisions)
            collision.updateTc(firstCollision.getTc());

        return fp;
    }

    public double computeCollisions() {
        List<Particle> toAnalyze;
        if (firstIter) {
            toAnalyze = new ArrayList<>(particleMap.values());
            firstIter = false;
        } else {
            toAnalyze = new ArrayList<>(particlesToAnalyze);
        }

        List<Particle> pending = analyzeCollision(toAnalyze);
        double fp = evolveState();
        particlesToAnalyze.addAll(pending); // TODO: Hacer mejor
        return fp;
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

        return new Collision(particle.getId(), ordinal, minTc);
    }

    private List<Particle> analyzeCollision(List<Particle> toAnalyze) {
        List<Particle> pending = new ArrayList<>();
        toAnalyze.stream().filter(p -> !p.isStatic()).forEach(particleI -> {
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

            if (Double.compare(particleITc, minTc) < 0)
                return;

            if (collisionIndexes.containsKey(particleI.getId())) {
                Optional<Particle> other = removeCollision(particleI.getId());
                other.ifPresent(pending::add);
            }

            if (collisionIndexes.containsKey(toCollide)) {
                Optional<Particle> other = removeCollision(toCollide);
                other.ifPresent(pending::add);
            }

            Collision collision = new Collision(particleI.getId(), toCollide, minTc);

            collisionIndexes.put(particleI.getId(), collision);
            if (toCollide >= Walls.values().length) {
                collisionIndexes.put(toCollide, collision);
            }

            collisions.add(collision);
        });
        
        return pending;

    }

    private Optional<Particle> removeCollision(long particleId) {
        Collision toRemove = collisionIndexes.get(particleId);
        collisions.remove(toRemove);
        long otherIdx = toRemove.getIndexA();
        if (otherIdx == particleId)
            otherIdx = toRemove.getIndexB();

        if (otherIdx < Walls.values().length)
            return Optional.empty();

        collisionIndexes.remove(otherIdx);
        Particle other = particleMap.get(otherIdx);
        return Optional.of(other);
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