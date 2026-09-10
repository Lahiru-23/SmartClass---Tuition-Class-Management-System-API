package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.AttendanceRequest;
import lk.SmartClass.dto.response.AttendanceResponse;
import lk.SmartClass.entity.AttendanceRecord;
import lk.SmartClass.entity.Enrollment;
import lk.SmartClass.entity.Teacher;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.repository.AttendanceRepository;
import lk.SmartClass.repository.EnrollmentRepository;
import lk.SmartClass.repository.TeacherRepository;
import lk.SmartClass.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional
    public AttendanceResponse mark(AttendanceRequest request, String teacherUsername) {
        if (attendanceRepository.existsByEnrollmentIdAndDate(request.getEnrollmentId(), request.getDate())) {
            throw new BadRequestException("Attendance already marked for this student on this date");
        }
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new BadRequestException("Invalid enrollmentId: " + request.getEnrollmentId()));
        Teacher teacher = teacherRepository.findByUserUsername(teacherUsername)
                .orElseThrow(() -> new BadRequestException("Marking teacher not found"));

        AttendanceRecord record = AttendanceRecord.builder()
                .enrollment(enrollment)
                .date(request.getDate())
                .status(request.getStatus())
                .markedBy(teacher)
                .build();

        AttendanceRecord saved = attendanceRepository.save(record);
        log.info("Attendance marked: enrollment={}, date={}, status={}",
                enrollment.getId(), request.getDate(), request.getStatus());
        return toResponse(saved);
    }

    @Override
    public List<AttendanceResponse> getByEnrollment(Long enrollmentId) {
        return attendanceRepository.findByEnrollmentId(enrollmentId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<AttendanceResponse> getByClassAndDate(Long classId, LocalDate date) {
        return attendanceRepository.findByEnrollmentClassEntityIdAndDate(classId, date)
                .stream().map(this::toResponse).toList();
    }

    private AttendanceResponse toResponse(AttendanceRecord r) {
        return AttendanceResponse.builder()
                .id(r.getId())
                .enrollmentId(r.getEnrollment().getId())
                .studentName(r.getEnrollment().getStudent().getFullName())
                .date(r.getDate())
                .status(r.getStatus())
                .markedByName(r.getMarkedBy().getFullName())
                .build();
    }
}

