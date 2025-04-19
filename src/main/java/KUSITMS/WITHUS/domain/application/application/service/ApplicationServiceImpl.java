package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.position.entity.Position;
import KUSITMS.WITHUS.domain.application.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.application.template.entity.ApplicationTemplate;
import KUSITMS.WITHUS.domain.application.template.repository.ApplicationTemplateRepository;
import KUSITMS.WITHUS.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ApplicationTemplateRepository templateRepository;
    private final PositionRepository positionRepository;

    /**
     * 지원서 생성
     * @param request 지원서 생성 요청 DTO
     * @return 생성된 지원서 정보
     */
    @Override
    @Transactional
    public ApplicationResponseDTO.Detail create(ApplicationRequestDTO.Create request) {
        ApplicationTemplate template = templateRepository.getById(request.templateId());
        Position position = positionRepository.getById(request.positionId());

        Application application = Application.create(
                request.name(),
                request.gender(),
                request.email(),
                request.phoneNumber(),
                request.university(),
                request.major(),
                request.birthDate(),
                request.imageUrl(),
                template,
                position
        );

        return ApplicationResponseDTO.Detail.from(applicationRepository.save(application));
    }

    /**
     * 지원서 삭제
     * @param id 삭제할 지원서 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        applicationRepository.getById(id);
        applicationRepository.delete(id);
    }

    /**
     * ID로 지원서 단건 조회
     * @param id 조회할 지원서 ID
     * @return 조회한 지원서 정보
     */
    @Override
    public ApplicationResponseDTO.Detail getById(Long id) {
        Application application = applicationRepository.getById(id);
        return ApplicationResponseDTO.Detail.from(application);
    }

    /**
     * 특정 공고의 지원서 전체 조회
     * @param recruitmentId 조회할 공고의 ID
     * @return 조회한 공고의 지원서 전제의 정보
     */
    @Override
    public List<ApplicationResponseDTO.Summary> getByRecruitmentId(Long recruitmentId) {
        return applicationRepository.findByRecruitmentId(recruitmentId).stream()
                .map(ApplicationResponseDTO.Summary::from)
                .toList();
    }
}
