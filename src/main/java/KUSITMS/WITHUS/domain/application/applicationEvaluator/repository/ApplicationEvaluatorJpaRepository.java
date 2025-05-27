package KUSITMS.WITHUS.domain.application.applicationEvaluator.repository;

import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationEvaluatorJpaRepository extends JpaRepository<ApplicationEvaluator, Long> {
    void deleteAllByApplication_Recruitment_Id(Long recruitmentId);
    void deleteAllByApplication_Id(Long applicationId);
}
