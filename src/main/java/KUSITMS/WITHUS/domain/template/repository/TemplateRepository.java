package KUSITMS.WITHUS.domain.template.repository;

import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.TemplateType;

import java.util.List;

public interface TemplateRepository {
    Template getById(Long templateId);
    List<Template> findAllByTemplateType(TemplateType templateType);
    Template save(Template template);
}
