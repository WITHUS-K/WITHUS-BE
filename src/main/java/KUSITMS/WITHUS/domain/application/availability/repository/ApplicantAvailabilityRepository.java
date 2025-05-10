package KUSITMS.WITHUS.domain.application.availability.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.availability.entity.ApplicantAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicantAvailabilityRepository extends JpaRepository<ApplicantAvailability, Long> {
    List<ApplicantAvailability> findByApplicationIn(List<Application> applications);
    List<ApplicantAvailability> findByApplicationId(Long id);
}
