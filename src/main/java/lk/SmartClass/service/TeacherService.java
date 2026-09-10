package lk.SmartClass.service;

import lk.SmartClass.dto.request.TeacherRequest;
import lk.SmartClass.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    TeacherResponse create(TeacherRequest request);
    TeacherResponse getById(Long id);
    List<TeacherResponse> getAll();
    TeacherResponse update(Long id, TeacherRequest request);
    void delete(Long id);
}
