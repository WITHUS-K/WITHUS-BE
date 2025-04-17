package KUSITMS.WITHUS.domain.application.position.repository;

import KUSITMS.WITHUS.domain.application.position.entity.Position;

import java.util.List;

public interface PositionRepository {
    Position getById(Long id);
    Position save(Position position);
    void delete(Long id);
    List<Position> findByOrganizationId(Long organizationId);
}
