package main.java.ar.edu.itba.ss.utils;

public class Configuration {
    public static double height;
    public static double width;
    public static double slitHeight;

    public static void setHeight(double height) {
        Configuration.height = height;
    }

    public static void setWidth(double width) {
        Configuration.width = width;
    }

    public static void setSlitHeight(double slitHeight) {
        Configuration.slitHeight = slitHeight;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getSlitHeight() {
        return slitHeight;
    }
}