package lk.SmartClass.service;

import lk.SmartClass.dto.request.StudentRequest;
import lk.SmartClass.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse create(StudentRequest request);
    StudentResponse getById(Long id);
    List<StudentResponse> getAll();
    StudentResponse update(Long id, StudentRequest request);
    void delete(Long id);
}