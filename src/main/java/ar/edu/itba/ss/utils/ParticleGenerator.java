package main.java.ar.edu.itba.ss.utils;

import main.java.ar.edu.itba.ss.models.Particle;
import main.java.ar.edu.itba.ss.models.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator {
    public static List<Particle> generate(String staticFile, int totalParticles, double height, double width,
                                          double speed, double mass, double radius) {
        List<Particle> particles = new ArrayList<>();
        Point position;
        boolean isColliding;
        for (int i = 0; i < totalParticles; i++) {

            Particle newParticle = new Particle(speed, mass, radius);
            do {
                position = randomPosition(height, width, radius);
                isColliding = false;
                for (Particle p : particles) {
                    if (Math.pow(position.getX() - p.getPosition().getX(), 2) +
                            Math.pow(position.getY() - p.getPosition().getY(), 2) <= Math.pow(radius, 2)) {
                        isColliding = true;
                        break;
                    }
                }
            } while (isColliding);

            newParticle.setPosition(position);
            particles.add(newParticle);
        }

        //TODO: imprimimos a archivo las particulas
        try (FileWriter writer = new FileWriter(staticFile)) {
            writer.write(totalParticles + "\n");
            writer.write("Space width" + width + "\n");
            writer.write("Space height" + height + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return particles;
    }

    private static Point randomPosition(double height, double width, double radius) {
        double x = Math.random() * width - radius;
        double y = Math.random() * height - radius;

        return new Point(x, y);
    }
}
