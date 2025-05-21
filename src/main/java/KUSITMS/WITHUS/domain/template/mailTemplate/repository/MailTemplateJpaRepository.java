package KUSITMS.WITHUS.domain.template.mailTemplate.repository;

import KUSITMS.WITHUS.domain.template.mailTemplate.entity.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTemplateJpaRepository extends JpaRepository<MailTemplate, Long> {
}
