package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    // Any logged-in user (admin or student): browse / search
    @GetMapping("/api/books")
    public List<Book> search(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    // Admin only (the interceptor blocks /api/admin/** for students)
    @PostMapping("/api/admin/books")
    public Book add(@Valid @RequestBody Book book) {
        return service.add(book);
    }

    @PutMapping("/api/admin/books/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody Book book) {
        return service.update(id, book);
    }

    @DeleteMapping("/api/admin/books/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
