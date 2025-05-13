package KUSITMS.WITHUS.global.infra.email.mailTemplate.repository;

import KUSITMS.WITHUS.global.infra.email.mailTemplate.entity.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTemplateJpaRepository extends JpaRepository<MailTemplate, Long> {
}
