package KUSITMS.WITHUS.domain.recruitment.position.service;

import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionResponseDTO;

import java.util.List;

public interface PositionService {
    PositionResponseDTO.Detail create(PositionRequestDTO.Create request);
    void delete(Long id);
    List<PositionResponseDTO.Detail> findAllByRecruitmentId(Long recruitmentId);
}
