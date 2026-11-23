package org.projectharvester.app;

/** Invalidates stale asynchronous previews whenever a newer request or edit occurs. */
public final class PreviewGate {
    private int generation = 0;
    public synchronized int next() { return ++generation; }
    public synchronized void invalidate() { generation++; }
    public synchronized boolean isCurrent(int token) { return token == generation; }
}
