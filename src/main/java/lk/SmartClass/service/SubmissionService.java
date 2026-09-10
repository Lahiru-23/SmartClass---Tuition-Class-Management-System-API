package lk.SmartClass.service;

import lk.SmartClass.dto.request.GradeSubmissionRequest;
import lk.SmartClass.dto.request.SubmissionRequest;
import lk.SmartClass.dto.response.SubmissionResponse;

import java.util.List;

public interface SubmissionService {
    SubmissionResponse submit(SubmissionRequest request);
    SubmissionResponse grade(Long id, GradeSubmissionRequest request);
    List<SubmissionResponse> getByAssignment(Long assignmentId);
    List<SubmissionResponse> getByStudent(Long studentId);
}