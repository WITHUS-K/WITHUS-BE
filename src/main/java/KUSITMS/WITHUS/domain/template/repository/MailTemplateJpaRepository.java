package KUSITMS.WITHUS.domain.template.repository;

import KUSITMS.WITHUS.domain.template.entity.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTemplateJpaRepository extends JpaRepository<MailTemplate, Long> {
}
