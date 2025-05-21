package KUSITMS.WITHUS.domain.template.service;

import KUSITMS.WITHUS.domain.template.dto.MailTemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.MailTemplateResponseDTO;

import java.util.List;

public interface MailTemplateService {
    List<MailTemplateResponseDTO.Summary> listAll();
    MailTemplateResponseDTO.Detail create(MailTemplateRequestDTO.Create dto);
}