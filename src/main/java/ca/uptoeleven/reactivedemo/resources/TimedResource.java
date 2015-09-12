package ca.uptoeleven.reactivedemo.resources;

public abstract class TimedResource {
    protected long startTime;

    protected void start() {
        this.startTime = System.currentTimeMillis();
    }

    protected long stop() {
        long stopTime = System.currentTimeMillis();
        return stopTime - startTime;
    }
}
