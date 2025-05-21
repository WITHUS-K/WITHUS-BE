package KUSITMS.WITHUS.domain.template.service;

import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.dto.TemplateResponseDTO;
import KUSITMS.WITHUS.domain.template.entity.Template;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
import KUSITMS.WITHUS.domain.template.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

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
     * @return 모든 메일 템플릿의 요약 정보 리스트
     */
    @Override
    public List<TemplateResponseDTO.Summary> listAll(Medium medium) {
        return templateRepository.findAllByTemplateType(medium).stream()
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
        Template ent = new Template(dto.name(), dto.subject(), dto.body(), dto.medium());
        Template saved = templateRepository.save(ent);
        return TemplateResponseDTO.Detail.from(saved);
    }

}
