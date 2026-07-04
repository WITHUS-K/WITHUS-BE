package KUSITMS.WITHUS.domain.application.applicationAcquaintance.repository;

import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationAcquaintanceJpaRepository extends JpaRepository<ApplicationAcquaintance, Long> {
    boolean existsByApplication_IdAndUser_Id(Long applicationId, Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from ApplicationAcquaintance acquaintance
            where acquaintance.application.id = :applicationId
              and acquaintance.user.id = :userId
            """)
    void deleteByApplication_IdAndUser_Id(@Param("applicationId") Long applicationId, @Param("userId") Long userId);
}
