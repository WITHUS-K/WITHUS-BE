package KUSITMS.WITHUS.domain.template.service;

import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.TemplateResponseDTO;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.util.List;

public interface TemplateService {
    TemplateResponseDTO.Detail getById(Long templateId);
    List<TemplateResponseDTO.Summary> listAll(Medium medium, User user);
    TemplateResponseDTO.Detail create(TemplateRequestDTO.Create dto);
    TemplateResponseDTO.Detail update(Long templateId, TemplateRequestDTO.Update dto);
}
