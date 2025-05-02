package KUSITMS.WITHUS.domain.recruitment.recruitment.service;

import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;

import java.util.List;

public interface RecruitmentService {
    RecruitmentResponseDTO.Create saveDraft(RecruitmentRequestDTO.Upsert request);
    RecruitmentResponseDTO.Create publish(RecruitmentRequestDTO.Upsert request);
    RecruitmentResponseDTO.Detail getById(Long id);
    RecruitmentResponseDTO.Update update(Long id, RecruitmentRequestDTO.Update request);
    void delete(Long id);
    List<RecruitmentResponseDTO.Summary> getAllByKeyword(String keyword);
}
