package KUSITMS.WITHUS.domain.application.service;

import KUSITMS.WITHUS.domain.application.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.application.dto.PositionResponseDTO;

import java.util.List;

public interface PositionService {
    PositionResponseDTO.Detail create(PositionRequestDTO.Create request);
    void delete(Long id);
    List<PositionResponseDTO.Detail> getByOrganization(Long organizationId);
}
