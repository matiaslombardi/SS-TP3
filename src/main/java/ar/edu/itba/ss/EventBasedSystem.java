package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.Particle;
import main.java.ar.edu.itba.ss.models.Space;
import main.java.ar.edu.itba.ss.utils.ParticleGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EventBasedSystem {
    final static int MAX_ITER = 1000;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java EventBasedSystem <particle_count> <epsilon>");
            System.exit(1);
        }

        int particleAmount = Integer.parseInt(args[0]);
        double fpEpsilon = Double.parseDouble(args[1]);

        double speed = 0.01;
        double mass = 1;
        double radius = 0.015;
        double height = 0.09;
        double width = 0.24;
        double slit = 0.01;

        List<Particle> particles = ParticleGenerator.generate("static.txt", particleAmount,
                height, width / 2, speed, mass, radius);

        Space space = new Space(height, width, slit, particles);

        try (FileWriter outFile = new FileWriter("out.txt")) {
            for (int i = 0; i < MAX_ITER; i++) {
                particles = space.getParticleMap().values().stream().toList();//TODO: mirar
                outFile.write(particleAmount + "\n");
                outFile.write("iter " + i + "\n");

                for (Particle p : particles)
                    outFile.write(String.format(Locale.ROOT, "%d %f %f\n", p.getId(),
                            p.getPosition().getX(), p.getPosition().getY()));

                //space.update();
            }
//            System.out.println("Iterations: " + iterCount);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
