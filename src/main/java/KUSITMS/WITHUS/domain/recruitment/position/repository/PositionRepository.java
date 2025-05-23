package KUSITMS.WITHUS.domain.recruitment.position.repository;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;

import java.util.List;
import java.util.Optional;

public interface PositionRepository {
    Optional<Position> findById(Long id);
    Position getById(Long id);
    Position save(Position position);
    void delete(Long id);
    Optional<Position> findByRecruitmentAndName(Recruitment recruitment, String name);
    List<Position> findAllByRecruitmentId(Long recruitmentId);
    List<Position> findByRecruitment_Id(Long recruitmentId);
}
