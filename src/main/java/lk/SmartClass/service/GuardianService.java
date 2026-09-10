package lk.SmartClass.service;

import lk.SmartClass.dto.request.GuardianRequest;
import lk.SmartClass.dto.response.GuardianResponse;

import java.util.List;

public interface GuardianService {
    GuardianResponse create(GuardianRequest request);
    GuardianResponse getById(Long id);
    List<GuardianResponse> getAll();
    GuardianResponse update(Long id, GuardianRequest request);
    void delete(Long id);
}