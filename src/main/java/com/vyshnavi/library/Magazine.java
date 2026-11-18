package com.vyshnavi.library;

/** A magazine: shorter 7-day loan, Rs 5 fine per overdue day. */
public class Magazine extends LibraryItem {
    private final int issueNumber;

    public Magazine(String id, String title, int issueNumber) {
        super(id, title);
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() { return issueNumber; }

    @Override public int getLoanDays() { return 7; }
    @Override public double getFinePerDay() { return 5.0; }
    @Override public String getType() { return "Magazine"; }
}
