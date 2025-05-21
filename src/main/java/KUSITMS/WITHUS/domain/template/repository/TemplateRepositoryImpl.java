package KUSITMS.WITHUS.domain.template.repository;

import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.TemplateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TemplateRepositoryImpl implements TemplateRepository{

    private final TemplateJpaRepository templateJpaRepository;

    @Override
    public List<Template> findAllByTemplateType(TemplateType templateType) {
        return templateJpaRepository.findAllByTemplateType(templateType);
    }

    @Override
    public Template save(Template template) {
        return templateJpaRepository.save(template);
    }
}
