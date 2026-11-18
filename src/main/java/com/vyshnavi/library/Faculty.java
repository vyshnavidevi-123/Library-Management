package com.vyshnavi.library;

/** Faculty can hold 5 items and are served first in the reservation queue. */
public class Faculty extends Member {
    public Faculty(String id, String name) { super(id, name); }

    @Override public int getMaxLoans() { return 5; }
    @Override public int getPriority() { return 0; }
}
