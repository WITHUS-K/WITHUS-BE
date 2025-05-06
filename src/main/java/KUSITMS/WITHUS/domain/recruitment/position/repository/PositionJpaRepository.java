package KUSITMS.WITHUS.domain.recruitment.position.repository;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionJpaRepository extends JpaRepository<Position, Long> {
}
