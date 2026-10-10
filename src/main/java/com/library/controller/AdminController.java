package com.library.controller;

import com.library.dto.Dtos.*;
import com.library.model.User;
import com.library.service.FineService;
import com.library.service.IssueService;
import com.library.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StudentService students;
    private final IssueService issues;
    private final FineService fines;

    @GetMapping("/stats")
    public Map<String, Long> stats() { return issues.stats(); }

    // ----- students -----
    @GetMapping("/students")
    public List<User> students() { return students.list(); }

    @PostMapping("/students")
    public User addStudent(@Valid @RequestBody RegisterRequest r) { return students.create(r); }

    @PutMapping("/students/{id}")
    public User updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateRequest r) {
        return students.update(id, r);
    }

    @DeleteMapping("/students/{id}")
    public void deleteStudent(@PathVariable Long id) { students.delete(id); }

    // ----- issue / return -----
    @GetMapping("/issues")
    public List<IssueDto> allIssues() { return issues.all(); }

    @GetMapping("/issues/overdue")
    public List<IssueDto> overdue() { return issues.overdue(); }

    @PostMapping("/issues")
    public IssueDto issue(@Valid @RequestBody IssueRequest r) { return issues.issue(r.bookId(), r.studentId()); }

    @PutMapping("/issues/{id}/return")
    public IssueDto returnBook(@PathVariable Long id) { return issues.returnBook(id); }

    // ----- fines -----
    @GetMapping("/fines")
    public List<FineDto> allFines() { return fines.all(); }

    @PutMapping("/fines/{id}/pay")
    public FineDto pay(@PathVariable Long id) { return fines.pay(id); }
}
