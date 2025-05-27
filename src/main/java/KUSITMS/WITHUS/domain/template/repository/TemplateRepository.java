package KUSITMS.WITHUS.domain.template.repository;

import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;

import java.util.List;

public interface TemplateRepository {
    Template getById(Long templateId);
    List<Template> findAllByMedium(Medium medium);
    Template save(Template template);
}
