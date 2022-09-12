package main.java.ar.edu.itba.ss.utils;

import java.awt.*;

@FunctionalInterface
public interface PointGetter {
    Point getPoint(double x, double y);
}
