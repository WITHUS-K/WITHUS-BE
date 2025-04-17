package KUSITMS.WITHUS.domain.application.template.repository;

import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationTemplateJpaRepository extends JpaRepository<ApplicationTemplate, Long> {
}
