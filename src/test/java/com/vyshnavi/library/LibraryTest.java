package com.vyshnavi.library;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryTest {
    private static final LocalDate DAY0 = LocalDate.of(2026, 11, 1);

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.addItem(new Book("B1", "Clean Code", "Robert C. Martin"));
        library.addItem(new Book("B2", "Algorithms", "Cormen"));
        library.addItem(new Book("B3", "Effective Java", "Bloch"));
        library.addItem(new Book("B4", "Data Structures", "Weiss"));
        library.addItem(new Magazine("M1", "Tech Monthly", 42));
        library.registerMember(new Student("S1", "Asha"));
        library.registerMember(new Student("S2", "Ravi"));
        library.registerMember(new Student("S3", "Meena"));
        library.registerMember(new Faculty("F1", "Dr. Rao"));
    }

    // ----- borrowing -----

    @Test
    void borrowMarksItemIssuedAndSetsDueDate() {
        Loan loan = library.borrow("S1", "B1", DAY0);
        assertFalse(library.getItem("B1").isAvailable());
        assertEquals(DAY0.plusDays(14), loan.getDueDate());
        assertEquals(1, library.getMember("S1").getActiveLoans().size());
    }

    @Test
    void magazineHasShorterLoanPeriodThanBook() {
        Loan loan = library.borrow("S1", "M1", DAY0);
        assertEquals(DAY0.plusDays(7), loan.getDueDate());
    }

    @Test
    void cannotBorrowItemThatIsAlreadyIssued() {
        library.borrow("S1", "B1", DAY0);
        LibraryException ex = assertThrows(LibraryException.class,
                () -> library.borrow("S2", "B1", DAY0));
        assertTrue(ex.getMessage().contains("currently issued"));
    }

    @Test
    void studentCannotExceedThreeItems() {
        library.borrow("S1", "B1", DAY0);
        library.borrow("S1", "B2", DAY0);
        library.borrow("S1", "B3", DAY0);
        assertThrows(LibraryException.class, () -> library.borrow("S1", "B4", DAY0));
    }

    @Test
    void facultyCanBorrowMoreThanStudent() {
        library.borrow("F1", "B1", DAY0);
        library.borrow("F1", "B2", DAY0);
        library.borrow("F1", "B3", DAY0);
        assertDoesNotThrow(() -> library.borrow("F1", "B4", DAY0));
    }

    @Test
    void unknownMemberOrItemIsRejected() {
        assertThrows(LibraryException.class, () -> library.borrow("X9", "B1", DAY0));
        assertThrows(LibraryException.class, () -> library.borrow("S1", "Z9", DAY0));
    }

    @Test
    void duplicateIdsAreRejected() {
        assertThrows(LibraryException.class,
                () -> library.addItem(new Book("B1", "Another", "Someone")));
        assertThrows(LibraryException.class,
                () -> library.registerMember(new Student("S1", "Copy")));
    }

    // ----- returning and fines -----

    @Test
    void returningOnTimeHasNoFine() {
        library.borrow("S1", "B1", DAY0);
        double fine = library.returnItem("S1", "B1", DAY0.plusDays(14));
        assertEquals(0.0, fine);
        assertTrue(library.getItem("B1").isAvailable());
        assertTrue(library.getMember("S1").getActiveLoans().isEmpty());
    }

    @Test
    void lateBookCostsTwoRupeesPerDay() {
        library.borrow("S1", "B1", DAY0);
        double fine = library.returnItem("S1", "B1", DAY0.plusDays(14 + 5));
        assertEquals(10.0, fine);
        assertEquals(10.0, library.getMember("S1").getTotalFines());
    }

    @Test
    void lateMagazineCostsFiveRupeesPerDay() {
        library.borrow("S1", "M1", DAY0);
        double fine = library.returnItem("S1", "M1", DAY0.plusDays(7 + 3));
        assertEquals(15.0, fine);
    }

    @Test
    void cannotReturnSomethingNotBorrowed() {
        assertThrows(LibraryException.class, () -> library.returnItem("S1", "B1", DAY0));
    }

    // ----- reservations (PriorityQueue) -----

    @Test
    void cannotReserveAvailableItem() {
        assertThrows(LibraryException.class, () -> library.reserve("S1", "B1"));
    }

    @Test
    void cannotReserveSameItemTwice() {
        library.borrow("S1", "B1", DAY0);
        library.reserve("S2", "B1");
        assertThrows(LibraryException.class, () -> library.reserve("S2", "B1"));
    }

    @Test
    void reservationsAreFirstComeFirstServedForSamePriority() {
        library.borrow("S1", "B1", DAY0);
        library.reserve("S2", "B1");
        library.reserve("S3", "B1");
        assertEquals("S2", library.nextInQueue("B1").getId());
    }

    @Test
    void facultyJumpsAheadOfStudentsInQueue() {
        library.borrow("S1", "B1", DAY0);
        library.reserve("S2", "B1");
        library.reserve("F1", "B1");
        assertEquals("F1", library.nextInQueue("B1").getId());
    }

    @Test
    void onlyNextPersonInQueueCanBorrowReturnedItem() {
        library.borrow("S1", "B1", DAY0);
        library.reserve("S2", "B1");
        library.returnItem("S1", "B1", DAY0.plusDays(2));

        assertThrows(LibraryException.class, () -> library.borrow("S3", "B1", DAY0.plusDays(3)));
        assertDoesNotThrow(() -> library.borrow("S2", "B1", DAY0.plusDays(3)));
        assertEquals(0, library.queueSize("B1"));
    }

    @Test
    void emptyQueueReturnsNull() {
        assertNull(library.nextInQueue("B1"));
    }

    // ----- searching (sort + binary search) -----

    @Test
    void catalogIsSortedByTitleIgnoringCase() {
        List<LibraryItem> sorted = library.getSortedCatalog();
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1).getTitle()
                    .compareToIgnoreCase(sorted.get(i).getTitle()) <= 0);
        }
    }

    @Test
    void binarySearchFindsEveryItemByTitle() {
        for (LibraryItem item : library.getSortedCatalog()) {
            assertSame(item, library.findByTitle(item.getTitle()));
        }
    }

    @Test
    void binarySearchIsCaseInsensitive() {
        assertEquals("B1", library.findByTitle("clean CODE").getId());
    }

    @Test
    void binarySearchReturnsNullWhenMissing() {
        assertNull(library.findByTitle("Not A Real Title"));
    }

    @Test
    void blankTitleIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Book("B9", " ", "Nobody"));
    }
}
