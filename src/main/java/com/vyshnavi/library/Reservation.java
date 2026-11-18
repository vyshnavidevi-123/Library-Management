package com.vyshnavi.library;

/**
 * A waitlist entry. Ordered by member priority first (faculty before students),
 * then by arrival order, so the PriorityQueue always yields the right next person.
 */
public class Reservation implements Comparable<Reservation> {
    private final Member member;
    private final long sequence;

    public Reservation(Member member, long sequence) {
        this.member = member;
        this.sequence = sequence;
    }

    public Member getMember() { return member; }

    @Override
    public int compareTo(Reservation other) {
        int byPriority = Integer.compare(member.getPriority(), other.member.getPriority());
        return byPriority != 0 ? byPriority : Long.compare(sequence, other.sequence);
    }
}
