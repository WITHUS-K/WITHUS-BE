package KUSITMS.WITHUS.domain.template.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.TemplateResponseDTO;
import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import KUSITMS.WITHUS.domain.template.repository.TemplateRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final OrganizationRepository organizationRepository;

    /**
     * 문자/메일 템플릿 개별 조회
     * @return 메일 템플릿의 상세 정보
     */
    @Override
    public TemplateResponseDTO.Detail getById(Long templateId) {
        Template template = templateRepository.getById(templateId);
        return TemplateResponseDTO.Detail.from(template);
    }

    /**
     * 문자/메일 템플릿 목록 조회
     * @return 자신이 속한 조직의 모든 메일 템플릿의 요약 정보 리스트
     */
    @Override
    public List<TemplateResponseDTO.Summary> listAll(Medium medium, User user) {
        List<Long> organizationIds = user.getUserOrganizations().stream()
                .map(uo -> uo.getOrganization().getId())
                .toList();

        return templateRepository.findAllByMedium(medium, organizationIds).stream()
                .map(TemplateResponseDTO.Summary::from)
                .toList();
    }

    /**
     * 문자/메일 템플릿 생성
     * @param dto 메일 템플릿 생성 요청 DTO
     * @return 생성된 메일 템플릿의 상세 정보
     */
    @Override
    @Transactional
    public TemplateResponseDTO.Detail create(TemplateRequestDTO.Create dto) {
        Organization organization = organizationRepository.getById(dto.organizationId());

        Template ent = new Template(dto.name(), dto.subject(), dto.body(), dto.medium(), organization);
        Template saved = templateRepository.save(ent);
        return TemplateResponseDTO.Detail.from(saved);
    }

    @Override
    @Transactional
    public TemplateResponseDTO.Detail update(Long templateId, TemplateRequestDTO.Update dto) {
        Template template = templateRepository.getById(templateId);

        // TODO: 사용자가 속한 조직의 템플릿만 수정하도록 검증 추가 필요

        template.update(
                dto.name(),
                dto.subject(),
                dto.body(),
                dto.medium()
        );

        return TemplateResponseDTO.Detail.from(template);
    }

}
