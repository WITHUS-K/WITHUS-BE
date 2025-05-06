package KUSITMS.WITHUS.domain.recruitment.position.repository;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;

import java.util.Optional;

public interface PositionRepository {
    Optional<Position> findById(Long id);
    Position getById(Long id);
    Position save(Position position);
    void delete(Long id);
}
