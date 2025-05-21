package KUSITMS.WITHUS.domain.template.repository;

import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TemplateRepositoryImpl implements TemplateRepository{

    private final TemplateJpaRepository templateJpaRepository;

    @Override
    public Template getById(Long templateId) {
        return templateJpaRepository.findById(templateId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEMPLATE_NOT_FOUND));
    }

    @Override
    public List<Template> findAllByTemplateType(Medium medium) {
        return templateJpaRepository.findAllByTemplateType(medium);
    }

    @Override
    public Template save(Template template) {
        return templateJpaRepository.save(template);
    }
}
