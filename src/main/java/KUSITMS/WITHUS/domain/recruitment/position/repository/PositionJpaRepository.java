package KUSITMS.WITHUS.domain.recruitment.position.repository;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionJpaRepository extends JpaRepository<Position, Long> {
    List<Position> findByRecruitment_Id(Long recruitmentId);
}
