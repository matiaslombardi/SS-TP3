package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.*;
import main.java.ar.edu.itba.ss.utils.Configuration;
import main.java.ar.edu.itba.ss.utils.ParticleGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EventBasedSystem {
    public static void main(String[] args) {
        System.out.println("Running");
        if (args.length != 3) {
            System.out.println("Usage: java EventBasedSystem <particle_count> <slit> <epsilon>");
            System.exit(1);
        }

        int particleAmount = Integer.parseInt(args[0]);
        double slit = Double.parseDouble(args[1]);
        double fpEpsilon = Double.parseDouble(args[2]);

        double speed = 0.01;
        double mass = 1;
        double radius = 0.0015;
        double height = 0.09;
        double width = 0.24;

        List<Particle> particles = ParticleGenerator.generate("static.txt", particleAmount,
                height, width / 2, speed, mass, radius);

        Configuration.setHeight(height);
        Configuration.setWidth(width);
        Configuration.setSlitHeight(slit);

        double borderHeight = (Configuration.height - Configuration.slitHeight) / 2;

        Particle slitParticle = new Particle(0, mass * 1000, 0, true);
        slitParticle.setPosition(new Point(width / 2, borderHeight));
        particles.add(slitParticle);

        slitParticle = new Particle(0, mass * 1000, 0, true);
        slitParticle.setPosition(new Point(width / 2, Configuration.height - borderHeight));
        particles.add(slitParticle);

        Space space = new Space(height, width, slit, particles);

        double eqTime = 0;
        boolean foundEq = false;
        int stopIter = 0;
        try (FileWriter outFile = new FileWriter("out.txt")) {
            double fp;
            for (int i = 0; i < stopIter || !foundEq; i++) {
                particles = space.getParticleMap().values().stream()
                        .filter(p -> !p.isStatic()).collect(Collectors.toList());

                outFile.write(particleAmount + "\n");
                outFile.write("iter " + i + "\n");

                Collision lastCollision = space.getLastCollision();

                if (lastCollision != null) {
                    boolean wallCollision = lastCollision.getIndexB() < Walls.values().length;
                    outFile.write(String.format("%b %d %d %f\n", wallCollision, lastCollision.getIndexA(), lastCollision.getIndexB(), lastCollision.getTc()));
                } else
                    outFile.write("\n");

                for (Particle p : particles) {
                    outFile.write(String.format(Locale.ROOT, "%d %f %f %f %f %f %f\n", p.getId(),
                            p.getPosition().getX(), p.getPosition().getY(), p.getSpeedX(), p.getSpeedY(), mass, radius));
                }

                fp = space.computeCollisions();

                if (!foundEq && Double.compare(Math.abs(fp - 0.5), fpEpsilon) <= 0) {
                    eqTime = space.getElapsedTime();
                    foundEq = true;
                    stopIter = i + 1000;
                }
            }

            outFile.write(String.format("%f %f\n", eqTime, space.getElapsedTime()));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
