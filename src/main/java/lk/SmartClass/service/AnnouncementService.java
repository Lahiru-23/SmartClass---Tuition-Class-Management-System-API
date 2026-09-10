package lk.SmartClass.service;

import lk.SmartClass.dto.request.AnnouncementRequest;
import lk.SmartClass.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponse post(AnnouncementRequest request, String postedByUsername);
    List<AnnouncementResponse> getByClass(Long classId);
    List<AnnouncementResponse> getInstituteWide();
}

