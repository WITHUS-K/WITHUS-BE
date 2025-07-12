package KUSITMS.WITHUS.domain.template.repository;

import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.organization.organization.entity.QOrganization.organization;
import static KUSITMS.WITHUS.domain.template.entity.QTemplate.template;

@Repository
@RequiredArgsConstructor
public class TemplateRepositoryImpl implements TemplateRepository{

    private final TemplateJpaRepository templateJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Template getById(Long templateId) {
        return templateJpaRepository.findById(templateId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEMPLATE_NOT_FOUND));
    }

    @Override
    public List<Template> findAllByMedium(Medium medium, List<Long> organizationIds) {
        return queryFactory.selectFrom(template)
                .join(template.organization, organization).fetchJoin()
                .where(
                        template.medium.eq(medium),
                        template.organization.id.in(organizationIds)
                )
                .fetch();
    }

    @Override
    public Template save(Template template) {
        return templateJpaRepository.save(template);
    }
}
