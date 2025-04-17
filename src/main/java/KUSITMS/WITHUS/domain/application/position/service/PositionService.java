package KUSITMS.WITHUS.domain.application.position.service;

import KUSITMS.WITHUS.domain.application.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.application.position.dto.PositionResponseDTO;

import java.util.List;

public interface PositionService {
    PositionResponseDTO.Detail create(PositionRequestDTO.Create request);
    void delete(Long id);
    List<PositionResponseDTO.Detail> getByOrganization(Long organizationId);
}
