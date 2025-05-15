package KUSITMS.WITHUS.domain.recruitment.recruitment.repository;

import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentJpaRepository extends JpaRepository<Recruitment, Long> {
    List<Recruitment> findByOrganization_IdIn(List<Long> organizationIds);
}
