package lk.SmartClass.service;

import lk.SmartClass.dto.request.EnrollmentRequest;
import lk.SmartClass.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enroll(EnrollmentRequest request);
    List<EnrollmentResponse> getByStudent(Long studentId);
    List<EnrollmentResponse> getByClass(Long classId);
    EnrollmentResponse updateStatus(Long id, String status);
    void drop(Long id);
}