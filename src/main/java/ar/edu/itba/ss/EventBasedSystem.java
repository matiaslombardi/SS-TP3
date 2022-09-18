package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.Particle;
import main.java.ar.edu.itba.ss.models.Point;
import main.java.ar.edu.itba.ss.models.Space;
import main.java.ar.edu.itba.ss.models.Walls;
import main.java.ar.edu.itba.ss.utils.ParticleGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EventBasedSystem {
    final static int MAX_ITER = 10000;

    public static void main(String[] args) {
<<<<<<< HEAD
        System.out.println("Estoy corriendo");
=======
        System.out.println("Running");
>>>>>>> Merge
        if (args.length != 2) {
            System.out.println("Usage: java EventBasedSystem <particle_count> <epsilon>");
            System.exit(1);
        }

        int particleAmount = Integer.parseInt(args[0]);
        double fpEpsilon = Double.parseDouble(args[1]);

        double speed = 0.01;
        double mass = 1;
        double radius = 0.0015;
        double height = 0.09;
        double width = 0.24;
        double slit = 0.01;

        List<Particle> particles = ParticleGenerator.generate("static.txt", particleAmount,
                height, width / 2, speed, mass, radius);

        double borderHeight = (Walls.HEIGHT - Walls.SLIT_HEIGHT) / 2;

        Particle slitParticle = new Particle(0, mass * 1000, 0, true);
        slitParticle.setPosition(new Point(width / 2, borderHeight));
        particles.add(slitParticle);

        slitParticle = new Particle(0, mass * 1000, 0, true);
        slitParticle.setPosition(new Point(width / 2, Walls.HEIGHT - borderHeight));
        particles.add(slitParticle);

        Space space = new Space(height, width, slit, particles);

        try (FileWriter outFile = new FileWriter("out.txt")) {
            double fp = 1;
            for (int i = 0; i < MAX_ITER && Double.compare(Math.abs(fp - 0.5), fpEpsilon) > 0; i++) {
                particles = space.getParticleMap().values().stream()
                        .filter(p -> !p.isStatic()).collect(Collectors.toList());

                outFile.write(particleAmount + "\n");
                outFile.write("iter " + i + "\n");

                for (Particle p : particles) {
                    outFile.write(String.format(Locale.ROOT, "%d %f %f %f\n", p.getId(),
                            p.getPosition().getX(), p.getPosition().getY(), radius)); //TODO: ver que devolvemos.
                }

                fp = space.computeCollisions();
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
