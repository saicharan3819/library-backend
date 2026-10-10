package com.library.service;

import com.library.dto.Dtos.FineDto;
import com.library.exception.ApiException;
import com.library.model.Fine;
import com.library.repository.FineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FineRepository fines;

    public List<FineDto> all() {
        return fines.findAllByOrderByFineDateDescIdDesc().stream().map(FineDto::from).toList();
    }

    public List<FineDto> forStudent(Long studentId) {
        return fines.findByStudentIdOrderByFineDateDescIdDesc(studentId).stream().map(FineDto::from).toList();
    }

    public FineDto pay(Long id) {
        Fine f = fines.findById(id).orElseThrow(() -> new ApiException(404, "Fine not found"));
        if (f.isPaid()) throw new ApiException(409, "Fine already paid");
        f.setPaid(true);
        f.setPaymentDate(LocalDate.now());
        return FineDto.from(fines.save(f));
    }
}
