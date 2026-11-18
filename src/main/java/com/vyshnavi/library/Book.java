package com.vyshnavi.library;

/** A book: 14-day loan, Rs 2 fine per overdue day. */
public class Book extends LibraryItem {
    private final String author;

    public Book(String id, String title, String author) {
        super(id, title);
        this.author = author;
    }

    public String getAuthor() { return author; }

    @Override public int getLoanDays() { return 14; }
    @Override public double getFinePerDay() { return 2.0; }
    @Override public String getType() { return "Book"; }
}
