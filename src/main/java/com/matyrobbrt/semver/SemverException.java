package com.matyrobbrt.semver;

/**
 * Thrown by {@link SemverAPI} methods when the semver library throws an exception.
 */
public class SemverException extends RuntimeException {
    public SemverException(String message) {
        super(message);
    }
}
