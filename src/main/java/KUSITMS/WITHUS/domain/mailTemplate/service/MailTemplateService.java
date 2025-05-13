package KUSITMS.WITHUS.domain.mailTemplate.service;

import KUSITMS.WITHUS.domain.mailTemplate.dto.MailTemplateRequestDTO;
import KUSITMS.WITHUS.domain.mailTemplate.dto.MailTemplateResponseDTO;

import java.util.List;

public interface MailTemplateService {
    List<MailTemplateResponseDTO.Summary> listAll();
    MailTemplateResponseDTO.Detail create(MailTemplateRequestDTO.Create dto);
}