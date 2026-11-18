# Smart Library Management System (Java)

A console-based library system built to practise **Object-Oriented Design**, **Data Structures & Algorithms**, **unit testing**, and **CI**.

![CI](../../actions/workflows/ci.yml/badge.svg)

## Features
- Add books and magazines, register students and faculty
- Borrow and return items with automatic due dates
- Overdue fines (Book: Rs 2/day, Magazine: Rs 5/day)
- Reservation waitlist - faculty are served before students, then first-come-first-served
- Title search using binary search on a sorted catalog

## OOP concepts used
| Concept | Where |
|---|---|
| Abstraction | `LibraryItem`, `Member` abstract classes |
| Inheritance | `Book`, `Magazine` extend `LibraryItem`; `Student`, `Faculty` extend `Member` |
| Polymorphism | Each subclass defines its own loan days, fine rate, borrow limit, priority |
| Encapsulation | Private fields, package-private mutators, controlled access through `Library` |
| Custom exceptions | `LibraryException` for rule violations |

## Data structures and algorithms used
| Structure / Algorithm | Purpose | Complexity |
|---|---|---|
| `HashMap` | Look up items and members by id | O(1) average |
| `PriorityQueue` | Reservation waitlist per item | O(log n) add/remove |
| Sorting + binary search | Find an item by title | O(n log n) sort, O(log n) search |

## Project structure
```
src/main/java/com/vyshnavi/library/   -> source code
src/test/java/com/vyshnavi/library/   -> JUnit 5 tests
.github/workflows/ci.yml              -> GitHub Actions pipeline
```

## Run it
Requires **Java 17+** and **Maven**.

```bash
mvn test                                        # run unit tests
mvn compile
java -cp target/classes com.vyshnavi.library.Main   # start the console app
```

## Testing
JUnit 5 tests cover borrowing, loan limits, fines, reservation priority, binary search, and error cases. Run with `mvn test`.

## CI
Every push and pull request triggers GitHub Actions to build the project and run all tests (`mvn -B verify`).

## Possible improvements
- Save data to a file or SQLite database
- Add a REST API with Spring Boot
- Add search by author
