package com.vyshnavi.library;

/** Students can hold 3 items and have normal reservation priority. */
public class Student extends Member {
    public Student(String id, String name) { super(id, name); }

    @Override public int getMaxLoans() { return 3; }
    @Override public int getPriority() { return 1; }
}
