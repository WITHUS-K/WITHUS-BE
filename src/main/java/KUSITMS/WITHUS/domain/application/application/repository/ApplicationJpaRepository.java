package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {
    List<Application> findByRecruitmentId(Long recruitmentId);
    List<Application> findByRecruitmentIdAndStatusIn(
            Long recruitmentId,
            List<ApplicationStatus> statuses
    );
    List<Application> findByRecruitment_IdAndPosition_Id(Long recruitmentId, Long positionId);
    Long countByRecruitment_IdAndPosition_Id(Long recruitmentId, Long positionId);
}
