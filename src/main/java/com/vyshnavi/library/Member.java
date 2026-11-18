package com.vyshnavi.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A library member. Subclasses define the borrowing limit and queue priority. */
public abstract class Member {
    private final String id;
    private final String name;
    private final List<Loan> activeLoans = new ArrayList<>();
    private double totalFines;

    protected Member(String id, String name) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("id and name must not be blank");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getTotalFines() { return totalFines; }
    public List<Loan> getActiveLoans() { return Collections.unmodifiableList(activeLoans); }

    /** Maximum number of items this member can hold at once. */
    public abstract int getMaxLoans();

    /** Reservation priority: a LOWER number is served first. */
    public abstract int getPriority();

    void addLoan(Loan loan) { activeLoans.add(loan); }
    void removeLoan(Loan loan) { activeLoans.remove(loan); }
    void addFine(double amount) { totalFines += amount; }
}
