package KUSITMS.WITHUS.domain.application.repository;

import KUSITMS.WITHUS.domain.application.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionJpaRepository extends JpaRepository<Position, Long> {
}
