package KUSITMS.WITHUS.domain.application.template.service;

import KUSITMS.WITHUS.domain.application.template.dto.ApplicationTemplateRequestDTO;
import KUSITMS.WITHUS.domain.application.template.dto.ApplicationTemplateResponseDTO;

public interface ApplicationTemplateService {
    ApplicationTemplateResponseDTO.Detail create(ApplicationTemplateRequestDTO.Create request);
    ApplicationTemplateResponseDTO.Detail getById(Long id);
    void delete(Long id);
}
