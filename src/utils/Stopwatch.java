package utils;

public class Stopwatch {

    private long start;

    public void StartStopwatch() {
        start = System.currentTimeMillis();
    }

    public void ResetStopwatch() {
        start = System.currentTimeMillis();
    }

    public double getElapsedTimeSeconds() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }

}
