package KUSITMS.WITHUS.domain.application.applicationAcquaintance.repository;

import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;

public interface ApplicationAcquaintanceRepository {
    ApplicationAcquaintance getById(Long id);
    boolean existsByApplication_IdAndUser_Id(Long applicationId, Long userId);
    void deleteByApplication_IdAndUser_Id(Long applicationId, Long userId);
    void save(ApplicationAcquaintance applicationAcquaintance);
}
