package utils;

public class Timer {

    public long duration;
    public long end;
    public long start;

    public Timer(long a) {
        start = System.currentTimeMillis();
        end = System.currentTimeMillis() + a;
        duration = a;
    }

    public Timer  () {

    }

    public Timer(long a , long b) {
        start = System.currentTimeMillis();
        end = System.currentTimeMillis() + a;
        duration = a;
    }



    public long getElapsed() {
        return System.currentTimeMillis() - start;
    }

    public long getDuration() {
        return duration;
    }

    public long getEnd() {
        return end;
    }

    public long getStart() {
        return start;
    }

    public void reset() {
        start = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= end;
    }

    public String getTimeRunningString(long time) {
        StringBuilder string = new StringBuilder();
        long totalSeconds = time / 1000L;
        long totalMinutes = totalSeconds / 60L;
        long totalHours = totalMinutes / 60L;
        int seconds = (int) totalSeconds % 60;
        int minutes = (int) totalMinutes % 60;
        int hours = (int) totalHours % 24;
        if (hours > 0) {
            string.append((new StringBuilder(String.valueOf(hours))).append(
                    ":").toString());
        }
        if (minutes > 0) {
            string.append((new StringBuilder(String.valueOf(minutes))).append(
                    ":").toString());
        }
        string.append((new StringBuilder(String.valueOf(seconds))).append(":")
                .toString());
        return string.toString();
    }
}
