package com.vyshnavi.library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Core service. Data structures used:
 *  - HashMap       : O(1) lookup of items and members by id
 *  - PriorityQueue : reservation waitlist per item (faculty first, then FIFO)
 *  - Sorted list + binary search : fast title search in the catalog
 */
public class Library {
    private final Map<String, LibraryItem> items = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, PriorityQueue<Reservation>> waitlists = new HashMap<>();
    private long reservationCounter = 0;

    // ---------- setup ----------

    public void addItem(LibraryItem item) {
        if (items.containsKey(item.getId())) {
            throw new LibraryException("Item id already exists: " + item.getId());
        }
        items.put(item.getId(), item);
    }

    public void registerMember(Member member) {
        if (members.containsKey(member.getId())) {
            throw new LibraryException("Member id already exists: " + member.getId());
        }
        members.put(member.getId(), member);
    }

    public LibraryItem getItem(String itemId) {
        LibraryItem item = items.get(itemId);
        if (item == null) throw new LibraryException("No such item: " + itemId);
        return item;
    }

    public Member getMember(String memberId) {
        Member member = members.get(memberId);
        if (member == null) throw new LibraryException("No such member: " + memberId);
        return member;
    }

    // ---------- borrowing ----------

    public Loan borrow(String memberId, String itemId, LocalDate date) {
        Member member = getMember(memberId);
        LibraryItem item = getItem(itemId);

        if (!item.isAvailable()) {
            throw new LibraryException("'" + item.getTitle() + "' is currently issued. You can reserve it.");
        }
        if (member.getActiveLoans().size() >= member.getMaxLoans()) {
            throw new LibraryException(member.getName() + " has reached the limit of "
                    + member.getMaxLoans() + " items.");
        }

        PriorityQueue<Reservation> queue = waitlists.get(itemId);
        if (queue != null && !queue.isEmpty()) {
            if (queue.peek().getMember() != member) {
                throw new LibraryException("'" + item.getTitle() + "' is reserved for "
                        + queue.peek().getMember().getName() + ".");
            }
            queue.poll(); // reservation fulfilled
        }

        Loan loan = new Loan(item, member, date);
        item.setAvailable(false);
        member.addLoan(loan);
        return loan;
    }

    /** Returns the fine owed (0 if returned on time). */
    public double returnItem(String memberId, String itemId, LocalDate date) {
        Member member = getMember(memberId);
        LibraryItem item = getItem(itemId);

        Loan loan = member.getActiveLoans().stream()
                .filter(l -> l.getItem() == item)
                .findFirst()
                .orElseThrow(() -> new LibraryException(member.getName()
                        + " has not borrowed '" + item.getTitle() + "'."));

        double fine = loan.calculateFine(date);
        member.removeLoan(loan);
        member.addFine(fine);
        item.setAvailable(true);
        return fine;
    }

    // ---------- reservations ----------

    public void reserve(String memberId, String itemId) {
        Member member = getMember(memberId);
        LibraryItem item = getItem(itemId);

        if (item.isAvailable()) {
            throw new LibraryException("'" + item.getTitle() + "' is available - just borrow it.");
        }
        PriorityQueue<Reservation> queue = waitlists.computeIfAbsent(itemId, k -> new PriorityQueue<>());
        boolean alreadyQueued = queue.stream().anyMatch(r -> r.getMember() == member);
        if (alreadyQueued) {
            throw new LibraryException(member.getName() + " already reserved '" + item.getTitle() + "'.");
        }
        queue.add(new Reservation(member, reservationCounter++));
    }

    /** Who is next in line for this item, or null if nobody is waiting. */
    public Member nextInQueue(String itemId) {
        PriorityQueue<Reservation> queue = waitlists.get(itemId);
        return (queue == null || queue.isEmpty()) ? null : queue.peek().getMember();
    }

    public int queueSize(String itemId) {
        PriorityQueue<Reservation> queue = waitlists.get(itemId);
        return queue == null ? 0 : queue.size();
    }

    // ---------- searching ----------

    /** Catalog sorted A-Z by title (case-insensitive). */
    public List<LibraryItem> getSortedCatalog() {
        List<LibraryItem> sorted = new ArrayList<>(items.values());
        sorted.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        return sorted;
    }

    /** Binary search on the sorted catalog: O(log n). Returns null if not found. */
    public LibraryItem findByTitle(String title) {
        List<LibraryItem> sorted = getSortedCatalog();
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = sorted.get(mid).getTitle().compareToIgnoreCase(title);
            if (cmp == 0) return sorted.get(mid);
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }
}
