package KUSITMS.WITHUS.domain.recruitment.service;

import KUSITMS.WITHUS.domain.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.dto.RecruitmentResponseDTO;

import java.util.List;

public interface RecruitmentService {
    RecruitmentResponseDTO.Create create(RecruitmentRequestDTO.Create request);
    RecruitmentResponseDTO.Detail getById(Long id);
    RecruitmentResponseDTO.Update update(Long id, RecruitmentRequestDTO.Update request);
    void delete(Long id);
    List<RecruitmentResponseDTO.Summary> getAllByKeyword(String keyword);
}
