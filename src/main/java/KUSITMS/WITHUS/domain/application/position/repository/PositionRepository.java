package KUSITMS.WITHUS.domain.application.position.repository;

import KUSITMS.WITHUS.domain.application.position.entity.Position;

import java.util.Optional;

public interface PositionRepository {
    Optional<Position> findById(Long id);
    Position getById(Long id);
    Position save(Position position);
    void delete(Long id);
}
