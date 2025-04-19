package KUSITMS.WITHUS.domain.application.template.repository;

import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;

public interface ApplicationTemplateRepository {
    ApplicationTemplate getById(Long id);
    ApplicationTemplate save(ApplicationTemplate template);
    void delete(Long id);
}
