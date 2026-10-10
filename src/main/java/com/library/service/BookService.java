package com.library.service;

import com.library.exception.ApiException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.repository.IssuedBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository books;
    private final IssuedBookRepository issues;

    public List<Book> search(String q) {
        return (q == null || q.isBlank()) ? books.findAll() : books.search(q.trim());
    }

    public Book add(Book b) {
        if (books.existsByIsbn(b.getIsbn())) throw new ApiException(409, "ISBN already exists");
        b.setId(null);
        b.setAvailableCopies(b.getTotalCopies());          // a new book starts with every copy on the shelf
        return books.save(b);
    }

    public Book update(Long id, Book in) {
        Book b = get(id);
        int issued = b.getTotalCopies() - b.getAvailableCopies();
        if (in.getTotalCopies() < issued)
            throw new ApiException(409, "Total copies cannot be less than issued copies (" + issued + ")");
        if (!b.getIsbn().equals(in.getIsbn()) && books.existsByIsbn(in.getIsbn()))
            throw new ApiException(409, "ISBN already exists");

        b.setTitle(in.getTitle());
        b.setAuthor(in.getAuthor());
        b.setIsbn(in.getIsbn());
        b.setCategory(in.getCategory());
        b.setPublisher(in.getPublisher());
        b.setEdition(in.getEdition());
        b.setPublicationYear(in.getPublicationYear());
        b.setShelfNumber(in.getShelfNumber());
        b.setTotalCopies(in.getTotalCopies());
        b.setAvailableCopies(in.getTotalCopies() - issued);   // keep issued count correct
        return books.save(b);
    }

    public void delete(Long id) {
        get(id);
        if (issues.existsByBookId(id))
            throw new ApiException(409, "Book has issue history and cannot be deleted");
        books.deleteById(id);
    }

    private Book get(Long id) {
        return books.findById(id).orElseThrow(() -> new ApiException(404, "Book not found"));
    }
}
