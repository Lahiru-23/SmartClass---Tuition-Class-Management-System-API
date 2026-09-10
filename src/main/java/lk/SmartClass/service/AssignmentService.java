package lk.SmartClass.service;

import lk.SmartClass.dto.response.AssignmentResponse;
import lk.SmartClass.dto.request.AssignmentRequest;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse create(AssignmentRequest request);
    AssignmentResponse getById(Long id);
    List<AssignmentResponse> getByClass(Long classId);
    AssignmentResponse update(Long id, AssignmentRequest request);
    void delete(Long id);
}
