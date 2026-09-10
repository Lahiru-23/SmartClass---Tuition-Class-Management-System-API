package lk.SmartClass.service;

import lk.SmartClass.dto.request.ClassRequest;
import lk.SmartClass.dto.response.ClassResponse;

import java.util.List;

public interface ClassService {
    ClassResponse create(ClassRequest request);
    ClassResponse getById(Long id);
    List<ClassResponse> getAll();
    ClassResponse update(Long id, ClassRequest request);
    void delete(Long id);
}