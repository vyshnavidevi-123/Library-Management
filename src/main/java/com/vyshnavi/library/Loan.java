package com.vyshnavi.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Record of one member borrowing one item. */
public class Loan {
    private final LibraryItem item;
    private final Member member;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    public Loan(LibraryItem item, Member member, LocalDate borrowDate) {
        this.item = item;
        this.member = member;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(item.getLoanDays());
    }

    public LibraryItem getItem() { return item; }
    public Member getMember() { return member; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }

    /** Fine owed if the item is returned on {@code returnDate} (0 if on time). */
    public double calculateFine(LocalDate returnDate) {
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return overdueDays > 0 ? overdueDays * item.getFinePerDay() : 0.0;
    }
}
