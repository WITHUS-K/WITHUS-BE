package KUSITMS.WITHUS.domain.application.applicationAcquaintance.repository;

import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationAcquaintanceJpaRepository extends JpaRepository<ApplicationAcquaintance, Long> {
    boolean existsByApplication_IdAndUser_Id(Long applicationId, Long userId);
    void deleteByApplication_IdAndUser_Id(Long applicationId, Long userId);
}