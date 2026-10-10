package com.library.config;

import com.library.model.Book;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Inserts a default admin, a demo student and sample books on the very first start. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository users;
    private final BookRepository books;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            users.save(user("Library Admin", "admin@library.com", "admin123", Role.ADMIN, "Library"));
            users.save(user("Demo Student", "student@library.com", "student123", Role.STUDENT, "Computer Science"));
        }
        if (books.count() == 0) {
            books.save(book("Clean Code", "Robert C. Martin", "9780132350884", "Programming", "Prentice Hall", "1st", 2008, 3, "A-01"));
            books.save(book("Effective Java", "Joshua Bloch", "9780134685991", "Programming", "Addison-Wesley", "3rd", 2018, 2, "A-02"));
            books.save(book("Database System Concepts", "Silberschatz", "9780078022159", "Database", "McGraw-Hill", "6th", 2010, 4, "B-01"));
            books.save(book("Introduction to Algorithms", "Cormen", "9780262033848", "Algorithms", "MIT Press", "3rd", 2009, 2, "B-02"));
            books.save(book("The Pragmatic Programmer", "Andrew Hunt", "9780135957059", "Programming", "Addison-Wesley", "2nd", 2019, 1, "C-01"));
        }
    }

    private User user(String name, String email, String pw, Role role, String dept) {
        User u = new User();
        u.setName(name); u.setEmail(email); u.setPassword(encoder.encode(pw));
        u.setRole(role); u.setDepartment(dept);
        return u;
    }

    private Book book(String title, String author, String isbn, String cat, String pub,
                      String edition, int year, int copies, String shelf) {
        Book b = new Book();
        b.setTitle(title); b.setAuthor(author); b.setIsbn(isbn); b.setCategory(cat);
        b.setPublisher(pub); b.setEdition(edition); b.setPublicationYear(year);
        b.setTotalCopies(copies); b.setAvailableCopies(copies); b.setShelfNumber(shelf);
        return b;
    }
}
