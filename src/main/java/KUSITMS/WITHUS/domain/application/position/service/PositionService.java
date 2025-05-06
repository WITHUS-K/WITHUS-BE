package KUSITMS.WITHUS.domain.application.position.service;

import KUSITMS.WITHUS.domain.application.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.application.position.dto.PositionResponseDTO;

public interface PositionService {
    PositionResponseDTO.Detail create(PositionRequestDTO.Create request);
    void delete(Long id);
}
