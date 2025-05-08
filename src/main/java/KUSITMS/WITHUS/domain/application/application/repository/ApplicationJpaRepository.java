package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {
    List<Application> findByRecruitmentId(Long recruitmentId);
}
