package KUSITMS.WITHUS.domain.template.service;

import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.TemplateResponseDTO;
import KUSITMS.WITHUS.domain.template.enumerate.TemplateType;

import java.util.List;

public interface TemplateService {
    List<TemplateResponseDTO.Summary> listAll(TemplateType templateType);
    TemplateResponseDTO.Detail create(TemplateRequestDTO.Create dto);
}