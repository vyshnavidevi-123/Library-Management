package com.vyshnavi.library;

/** Thrown when a library rule is broken (item unavailable, limit reached, unknown id...). */
public class LibraryException extends RuntimeException {
    public LibraryException(String message) {
        super(message);
    }
}
