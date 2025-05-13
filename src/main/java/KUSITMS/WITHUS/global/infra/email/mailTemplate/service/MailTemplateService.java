package KUSITMS.WITHUS.global.infra.email.mailTemplate.service;

import KUSITMS.WITHUS.global.infra.email.mailTemplate.dto.MailTemplateRequestDTO;
import KUSITMS.WITHUS.global.infra.email.mailTemplate.dto.MailTemplateResponseDTO;

import java.util.List;

public interface MailTemplateService {
    List<MailTemplateResponseDTO.Summary> listAll();
    MailTemplateResponseDTO.Detail create(MailTemplateRequestDTO.Create dto);
}