package lk.SmartClass.service;

import lk.SmartClass.dto.request.AttendanceRequest;
import lk.SmartClass.dto.response.AttendanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse mark(AttendanceRequest request, String teacherUsername);
    List<AttendanceResponse> getByEnrollment(Long enrollmentId);
    List<AttendanceResponse> getByClassAndDate(Long classId, LocalDate date);
}
