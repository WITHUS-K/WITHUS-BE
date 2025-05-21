package KUSITMS.WITHUS.domain.template.mailTemplate.service;

import KUSITMS.WITHUS.domain.template.mailTemplate.dto.MailTemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.mailTemplate.dto.MailTemplateResponseDTO;

import java.util.List;

public interface MailTemplateService {
    List<MailTemplateResponseDTO.Summary> listAll();
    MailTemplateResponseDTO.Detail create(MailTemplateRequestDTO.Create dto);
}