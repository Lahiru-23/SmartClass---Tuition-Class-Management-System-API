package lk.SmartClass.security;

import lk.SmartClass.entity.Enrollment;
import lk.SmartClass.entity.Student;
import lk.SmartClass.entity.User;
import lk.SmartClass.exception.ForbiddenOperationException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.EnrollmentRepository;
import lk.SmartClass.repository.StudentRepository;
import lk.SmartClass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority;

@Component
@RequiredArgsConstructor

public class AccessGuard {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public boolean isStaff(Authentication authentication) {
        return hasAuthority(authentication, "ROLE_ADMIN") || hasAuthority(authentication, "ROLE_TEACHER");
    }

    public Student currentStudent(Authentication authentication) {
        if (authentication == null) return null;
        return studentRepository.findByUserUsername(authentication.getName()).orElse(null);
    }

    public void requireSelfStudentOrStaff(Long studentId, Authentication authentication) {
        if (isStaff(authentication)) return;

        Student self = currentStudent(authentication);
        if (self == null || !self.getId().equals(studentId)) {
            throw new ForbiddenOperationException("You are not allowed to access another student's records");
        }
    }

    public void requireSelfUserOrStaff(Long userId, Authentication authentication) {
        if (isStaff(authentication)) return;
        if (authentication == null) {
            throw new ForbiddenOperationException("You are not allowed to access this resource");
        }
        User caller = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (caller == null || !caller.getId().equals(userId)) {
            throw new ForbiddenOperationException("You are not allowed to access another user's notifications");
        }
    }

    public void requireSelfEnrollmentOrStaff(Long enrollmentId, Authentication authentication) {
        if (isStaff(authentication)) return;
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        requireSelfStudentOrStaff(enrollment.getStudent().getId(), authentication);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        if (authentication == null) return false;
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if (authority.equals(a.getAuthority())) return true;
        }
        return false;
    }
}
