package com.trixit.application;

/** Port: tells whether the persistence tier is reachable. */
public interface DatabaseProbe {

    boolean isReachable();
}
