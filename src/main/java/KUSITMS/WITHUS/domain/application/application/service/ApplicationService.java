package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDTO.Detail create(ApplicationRequestDTO.Create request);
    void delete(Long id);
    ApplicationResponseDTO.Detail getById(Long id);
    List<ApplicationResponseDTO.Summary> getByRecruitmentId(Long recruitmentId);
}
