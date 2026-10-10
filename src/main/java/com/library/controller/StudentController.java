package com.library.controller;

import com.library.dto.Dtos.*;
import com.library.model.User;
import com.library.service.FineService;
import com.library.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Everything here is automatically limited to the logged-in student (set by AuthInterceptor). */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final IssueService issues;
    private final FineService fines;

    @GetMapping("/books/current")
    public List<IssueDto> current(@RequestAttribute("user") User user) { return issues.current(user.getId()); }

    @GetMapping("/books/history")
    public List<IssueDto> history(@RequestAttribute("user") User user) { return issues.history(user.getId()); }

    @GetMapping("/fines")
    public List<FineDto> fines(@RequestAttribute("user") User user) { return fines.forStudent(user.getId()); }
}
