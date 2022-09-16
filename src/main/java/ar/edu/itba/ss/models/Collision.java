package main.java.ar.edu.itba.ss.models;

public class Collision implements Comparable<Collision> {
    // TODO: como representamos pared vs particula?
    private long indexA;
    private long indexB;
    private double tc;

    private boolean isInvalid = false;

    public Collision(long indexA, long indexB, double tc) {
        this.indexA = indexA;
        this.indexB = indexB;
        this.tc = tc;
    }

    public long getIndexA() {
        return indexA;
    }

    public long getIndexB() {
        return indexB;
    }

    public void updateTc(double elapsed) {
        this.tc -= elapsed;
    }

    public double getTc() {
        return tc;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }

    @Override
    public int compareTo(Collision other) {
        return Double.compare(tc, other.getTc());
    }
}