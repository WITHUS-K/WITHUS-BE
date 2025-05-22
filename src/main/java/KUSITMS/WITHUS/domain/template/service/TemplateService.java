package KUSITMS.WITHUS.domain.template.service;

import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.TemplateResponseDTO;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;

import java.util.List;

public interface TemplateService {
    TemplateResponseDTO.Detail getById(Long templateId);
    List<TemplateResponseDTO.Summary> listAll(Medium medium);
    TemplateResponseDTO.Detail create(TemplateRequestDTO.Create dto);
}