package KUSITMS.WITHUS.domain.mailTemplate.repository;

import KUSITMS.WITHUS.domain.mailTemplate.entity.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTemplateJpaRepository extends JpaRepository<MailTemplate, Long> {
}
