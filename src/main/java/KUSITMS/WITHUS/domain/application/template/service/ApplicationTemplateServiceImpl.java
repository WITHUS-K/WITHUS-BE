package KUSITMS.WITHUS.domain.application.template.service;

import KUSITMS.WITHUS.domain.application.template.dto.ApplicationTemplateRequestDTO;
import KUSITMS.WITHUS.domain.application.template.dto.ApplicationTemplateResponseDTO;
import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import KUSITMS.WITHUS.domain.application.template.repository.ApplicationTemplateRepository;
import KUSITMS.WITHUS.domain.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationTemplateServiceImpl implements ApplicationTemplateService {

    private final ApplicationTemplateRepository applicationTemplateRepository;
    private final RecruitmentRepository recruitmentRepository;

    /**
     * 지원서 양식 생성
     * @param request 양식 생성 요청 DTO
     * @return 생성된 양식의 정보
     */
    @Override
    @Transactional
    public ApplicationTemplateResponseDTO.Detail create(ApplicationTemplateRequestDTO.Create request) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());

        ApplicationTemplate template = ApplicationTemplate.builder()
                .title(request.title())
                .recruitment(recruitment)
                .build();

        return ApplicationTemplateResponseDTO.Detail.from(applicationTemplateRepository.save(template));
    }

    /**
     * 지원서 양식 단건 조회
     * @param id 조회할 양식의 ID
     * @return 조회된 양식의 정보
     */
    @Override
    public ApplicationTemplateResponseDTO.Detail getById(Long id) {
        ApplicationTemplate template = applicationTemplateRepository.getById(id);

        return ApplicationTemplateResponseDTO.Detail.from(template);
    }

    /**
     * 지원서 양식 삭제
     * @param id 삭제할 양식의 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        applicationTemplateRepository.getById(id);
        applicationTemplateRepository.delete(id);
    }
}
