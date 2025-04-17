package KUSITMS.WITHUS.domain.application.repository;

import KUSITMS.WITHUS.domain.application.entity.Position;

import java.util.List;

public interface PositionRepository {
    Position getById(Long id);
    Position save(Position position);
    void delete(Long id);
    List<Position> findByOrganizationId(Long organizationId);
}
