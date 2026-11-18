package com.vyshnavi.library;

import java.time.LocalDate;
import java.util.Scanner;

/** Simple console menu to try the library system. */
public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        seedData(library);

        Scanner in = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n===== Smart Library =====");
            System.out.println("1. Show catalog (A-Z)");
            System.out.println("2. Search by exact title");
            System.out.println("3. Borrow an item");
            System.out.println("4. Return an item");
            System.out.println("5. Reserve an item");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            String choice = in.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> library.getSortedCatalog().forEach(System.out::println);
                    case "2" -> {
                        System.out.print("Title: ");
                        LibraryItem found = library.findByTitle(in.nextLine().trim());
                        System.out.println(found == null ? "Not found." : found);
                    }
                    case "3" -> {
                        System.out.print("Member id: ");
                        String m = in.nextLine().trim();
                        System.out.print("Item id: ");
                        Loan loan = library.borrow(m, in.nextLine().trim(), LocalDate.now());
                        System.out.println("Issued. Due on " + loan.getDueDate());
                    }
                    case "4" -> {
                        System.out.print("Member id: ");
                        String m = in.nextLine().trim();
                        System.out.print("Item id: ");
                        double fine = library.returnItem(m, in.nextLine().trim(), LocalDate.now());
                        System.out.println(fine > 0 ? "Returned late. Fine: Rs " + fine : "Returned on time.");
                    }
                    case "5" -> {
                        System.out.print("Member id: ");
                        String m = in.nextLine().trim();
                        System.out.print("Item id: ");
                        String i = in.nextLine().trim();
                        library.reserve(m, i);
                        System.out.println("Reserved. People in queue: " + library.queueSize(i));
                    }
                    case "6" -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (LibraryException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static void seedData(Library library) {
        library.addItem(new Book("B1", "Clean Code", "Robert C. Martin"));
        library.addItem(new Book("B2", "Introduction to Algorithms", "Cormen et al."));
        library.addItem(new Book("B3", "Effective Java", "Joshua Bloch"));
        library.addItem(new Magazine("M1", "Tech Monthly", 42));
        library.registerMember(new Student("S1", "Vyshnavi"));
        library.registerMember(new Student("S2", "Ravi"));
        library.registerMember(new Faculty("F1", "Dr. Rao"));
        System.out.println("Sample members: S1 (Student), S2 (Student), F1 (Faculty)");
        System.out.println("Sample items:   B1, B2, B3 (Books), M1 (Magazine)");
    }
}
