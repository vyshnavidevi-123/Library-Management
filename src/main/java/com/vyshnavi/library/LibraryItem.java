package com.vyshnavi.library;

/**
 * Abstraction: anything the library can lend. Subclasses decide their own
 * loan period and fine rate (polymorphism).
 */
public abstract class LibraryItem {
    private final String id;
    private final String title;
    private boolean available = true;

    protected LibraryItem(String id, String title) {
        if (id == null || id.isBlank() || title == null || title.isBlank()) {
            throw new IllegalArgumentException("id and title must not be blank");
        }
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return available; }
    void setAvailable(boolean available) { this.available = available; }

    /** Number of days the item can be kept before it is overdue. */
    public abstract int getLoanDays();

    /** Fine charged (in rupees) for every overdue day. */
    public abstract double getFinePerDay();

    /** Short label such as "Book" or "Magazine". */
    public abstract String getType();

    @Override
    public String toString() {
        return String.format("[%s] %-8s %-30s %s", id, getType(), title,
                available ? "Available" : "Issued");
    }
}
