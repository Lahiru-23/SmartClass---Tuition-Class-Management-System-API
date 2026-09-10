package lk.SmartClass.service.impl;

import lk.SmartClass.dto.request.GuardianRequest;
import lk.SmartClass.dto.response.GuardianResponse;
import lk.SmartClass.entity.Guardian;
import lk.SmartClass.entity.User;
import lk.SmartClass.exception.BadRequestException;
import lk.SmartClass.exception.ResourceNotFoundException;
import lk.SmartClass.repository.GuardianRepository;
import lk.SmartClass.repository.StudentRepository;
import lk.SmartClass.repository.UserRepository;
import lk.SmartClass.service.GuardianService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuardianServiceImpl implements GuardianService {

    private static final Logger log = LoggerFactory.getLogger(GuardianServiceImpl.class);

    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public GuardianResponse create(GuardianRequest request) {
        Guardian guardian = Guardian.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .user(resolveUser(request.getUserId(), null))
                .build();
        Guardian saved = guardianRepository.save(guardian);
        log.info("Guardian created: id={}, name='{}'", saved.getId(), saved.getFullName());
        return toResponse(saved);
    }

    @Override
    public GuardianResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<GuardianResponse> getAll() {
        return guardianRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public GuardianResponse update(Long id, GuardianRequest request) {
        Guardian guardian = findOrThrow(id);
        guardian.setFullName(request.getFullName());
        guardian.setPhone(request.getPhone());
        guardian.setAddress(request.getAddress());
        guardian.setUser(resolveUser(request.getUserId(), id));
        Guardian updated = guardianRepository.save(guardian);
        log.info("Guardian updated: id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Guardian guardian = findOrThrow(id);
        if (!studentRepository.findByGuardianId(id).isEmpty()) {
            throw new BadRequestException("Cannot delete guardian: still linked to one or more students");
        }
        guardianRepository.delete(guardian);
        log.info("Guardian deleted: id={}", id);
    }

    private Guardian findOrThrow(Long id) {
        return guardianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guardian not found with id: " + id));
    }

    private User resolveUser(Long userId, Long currentGuardianId) {
        if (userId == null) return null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Invalid userId: " + userId));
        guardianRepository.findByUserId(userId).ifPresent(existing -> {
            if (currentGuardianId == null || !existing.getId().equals(currentGuardianId)) {
                throw new BadRequestException("userId " + userId + " is already linked to another guardian");
            }
        });
        return user;
    }

    private GuardianResponse toResponse(Guardian g) {
        return GuardianResponse.builder()
                .id(g.getId())
                .fullName(g.getFullName())
                .phone(g.getPhone())
                .address(g.getAddress())
                .userId(g.getUser() != null ? g.getUser().getId() : null)
                .build();
    }
}
