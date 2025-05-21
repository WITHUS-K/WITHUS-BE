package KUSITMS.WITHUS.domain.template.repository;

import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateJpaRepository extends JpaRepository<Template, Long> {
    List<Template> findAllByMedium(Medium medium);
}
