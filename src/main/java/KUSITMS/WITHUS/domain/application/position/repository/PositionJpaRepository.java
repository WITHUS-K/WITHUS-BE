package KUSITMS.WITHUS.domain.application.position.repository;

import KUSITMS.WITHUS.domain.application.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionJpaRepository extends JpaRepository<Position, Long> {
}
