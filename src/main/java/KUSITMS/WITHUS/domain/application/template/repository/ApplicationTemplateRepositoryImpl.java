package KUSITMS.WITHUS.domain.application.template.repository;

import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationTemplateRepositoryImpl implements ApplicationTemplateRepository {

    private final ApplicationTemplateJpaRepository applicationTemplateJpaRepository;

    @Override
    public ApplicationTemplate getById(Long id) {
        return applicationTemplateJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TEMPLATE_NOT_EXIST));
    }

    @Override
    public ApplicationTemplate save(ApplicationTemplate template) {
        return applicationTemplateJpaRepository.save(template);
    }

    @Override
    public void delete(Long id) {
        applicationTemplateJpaRepository.deleteById(id);
    }
}
