package com.library.service;

import com.library.dto.Dtos.*;
import com.library.exception.ApiException;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.IssuedBookRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository users;
    private final IssuedBookRepository issues;
    private final PasswordEncoder encoder;

    public List<User> list() {
        return users.findByRole(Role.STUDENT);
    }

    public User create(RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ApiException(409, "Email is already registered");
        User u = new User();
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));     // never store plain text passwords
        u.setPhone(r.phone());
        u.setDepartment(r.department());
        u.setRole(Role.STUDENT);
        return users.save(u);
    }

    public User update(Long id, StudentUpdateRequest r) {
        User u = get(id);
        u.setName(r.name().trim());
        u.setPhone(r.phone());
        u.setDepartment(r.department());
        return users.save(u);
    }

    public void delete(Long id) {
        get(id);
        if (issues.existsByStudentId(id))
            throw new ApiException(409, "Student has issue history and cannot be deleted");
        users.deleteById(id);
    }

    private User get(Long id) {
        return users.findById(id).filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> new ApiException(404, "Student not found"));
    }
}
