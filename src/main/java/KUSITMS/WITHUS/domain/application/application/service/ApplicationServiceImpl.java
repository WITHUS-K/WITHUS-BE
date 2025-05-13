package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.enumerate.EvaluationStatus;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationJpaRepository;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerRequestDTO;
import KUSITMS.WITHUS.domain.application.applicationAnswer.entity.ApplicationAnswer;
import KUSITMS.WITHUS.domain.application.applicationAnswer.repository.ApplicationAnswerRepository;
import KUSITMS.WITHUS.domain.application.availability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.availability.repository.ApplicantAvailabilityRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluation.repository.EvaluationRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository.EvaluationCriteriaRepository;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.repository.DocumentQuestionRepository;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.upload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicantAvailabilityRepository applicantAvailabilityRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final PositionRepository positionRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final DocumentQuestionRepository documentQuestionRepository;
    private final ApplicationAnswerRepository applicationAnswerRepository;
    private final FileUploadService fileUploadService;
    private final ApplicationJpaRepository applicationJpaRepository;

    /**
     * 지원서 생성
     * @param request 지원서 생성 요청 DTO
     * @return 생성된 지원서 정보
     */
    @Override
    @Transactional
    public ApplicationResponseDTO.Summary create(ApplicationRequestDTO.Create request, MultipartFile profileImage, List<MultipartFile> files) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());
        Position position = Optional.ofNullable(request.positionId())
                .flatMap(positionRepository::findById)
                .orElse(null);

        validateRequiredFields(recruitment, request);

        Application application = createApplication(request, recruitment, position);
        Application savedApplication = applicationRepository.save(application);

        String imageUrl = fileUploadService.uploadProfileImage(profileImage, recruitment.getOrganization().getId(), recruitment.getId(), savedApplication.getId());
        savedApplication.updateImageUrl(imageUrl);

        saveApplicantAvailabilities(savedApplication, request.availableTimes());

        List<MultipartFile> fileList = files != null ? files : List.of();
        validateFileAnswers(recruitment, request.answers(), fileList);

        Map<String, String> uploadedFileUrls = fileUploadService.uploadAnswerFiles(files, recruitment.getOrganization().getId(), recruitment.getId(), savedApplication.getId());

        saveApplicationAnswers(savedApplication, recruitment, request, uploadedFileUrls, fileList);

        return ApplicationResponseDTO.Summary.from(savedApplication);
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
    public ApplicationResponseDTO.Detail getById(Long id, Long currentUserId) {
        Application application = applicationRepository.getById(id);
        List<ApplicantAvailability> availabilityList = applicantAvailabilityRepository.findByApplicationId(id);
        List<Evaluation> evaluationList = evaluationRepository.findEvaluationsForApplication(id);

        Long recruitmentId = application.getRecruitment().getId();
        List<EvaluationCriteria> evaluationCriteriaList = evaluationCriteriaRepository.findByTypeAndRecruitment(EvaluationType.DOCUMENT, recruitmentId);

        return ApplicationResponseDTO.Detail.from(application, availabilityList, evaluationList, evaluationCriteriaList, currentUserId);
    }

    /**
     * 특정 공고의 지원서 전체 조회
     * @param recruitmentId 조회할 공고의 ID
     * @return 조회한 공고의 지원서 전제의 정보
     */
    @Override
    public Page<ApplicationResponseDTO.SummaryForUser> getByRecruitmentId(
            Long recruitmentId,
            Long currentUserId,
            EvaluationStatus evaluationStatus,
            String keyword,
            Pageable pageable)
    {
        List<Application> allApplications = applicationJpaRepository.findByRecruitmentId(recruitmentId); // 전체 가져오기

        List<ApplicationResponseDTO.SummaryForUser> filtered = allApplications.stream()
                .map(app -> ApplicationResponseDTO.SummaryForUser.from(app, currentUserId))
                .filter(dto -> {
                    boolean statusMatch = switch (evaluationStatus) {
                        case EVALUATED -> dto.evaluated();
                        case NOT_EVALUATED -> !dto.evaluated();
                        case ALL -> true;
                    };

                    boolean keywordMatch = (keyword == null || keyword.isBlank()) ||
                            dto.name().toLowerCase().contains(keyword.toLowerCase());

                    return statusMatch && keywordMatch;
                })
                .toList();

        // 페이징 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<ApplicationResponseDTO.SummaryForUser> paged = start > end ? List.of() : filtered.subList(start, end);

        return new PageImpl<>(paged, pageable, filtered.size());
    }

    /**
     * 지원서 ID를 리스트로 받아서 일괄적으로 상태를 변경
     * @param request 일괄 요청 DTO (ID 리스트, 상태값)
     */
    @Override
    @Transactional
    public void updateStatus(ApplicationRequestDTO.UpdateStatus request) {
        List<Application> applications = request.applicationIds().stream()
                .map(applicationRepository::getById)
                .toList();

        applications.forEach(app -> app.updateStatus(request.status()));
    }

    private void validateRequiredFields(Recruitment recruitment, ApplicationRequestDTO.Create request) {
        if (recruitment.isNeedGender() && request.gender() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedSchool() && request.university() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedBirthDate() && request.birthDate() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedAcademicStatus() && request.major() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedAcademicStatus() && request.academicStatus() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
        if (recruitment.isNeedAddress() && request.address() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING);
        }
    }

    private Application createApplication(ApplicationRequestDTO.Create request, Recruitment recruitment, Position position) {
        return Application.create(
                request.name(),
                request.gender(),
                request.email(),
                request.phoneNumber(),
                request.university(),
                request.major(),
                request.academicStatus(),
                request.birthDate(),
                request.address(),
                recruitment,
                position
        );
    }

    private void saveApplicationAnswers(
            Application application,
            Recruitment recruitment,
            ApplicationRequestDTO.Create request,
            Map<String, String> uploadedFileUrls,
            List<MultipartFile> files
    ) {
        List<DocumentQuestion> questions = documentQuestionRepository.findByRecruitment(recruitment);
        Map<Long, DocumentQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(DocumentQuestion::getId, q -> q));

        Set<String> providedFileNames = files.stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toSet());

        // 파일명이 DTO에 존재하는지 검증
        for (ApplicationAnswerRequestDTO answer : request.answers()) {
            DocumentQuestion question = questionMap.get(answer.questionId());

            if (question.getType() == QuestionType.FILE) {
                if (answer.fileName() == null || !providedFileNames.contains(answer.fileName())) {
                    throw new CustomException(ErrorCode.FILE_NAME_NOT_MATCH);
                }
            }
        }

        // 파일 개수가 정확한지 검증
        long expectedFileCount = request.answers().stream()
                .filter(a -> {
                    DocumentQuestion question = questionMap.get(a.questionId());
                    return question.getType() == QuestionType.FILE;
                })
                .count();

        if (files.size() != expectedFileCount) {
            throw new CustomException(ErrorCode.FILE_COUNT_MISMATCH);
        }

        List<ApplicationAnswer> answers = request.answers().stream()
                .map(dto -> {
                    DocumentQuestion question = questionMap.get(dto.questionId());
                    String fileUrl = null;

                    if (question.getType() == QuestionType.FILE && dto.fileName() != null) {
                        fileUrl = uploadedFileUrls.get(dto.fileName());
                    }

                    return ApplicationAnswer.create(application, question, dto.answerText(), fileUrl);
                })
                .toList();

        applicationAnswerRepository.saveAll(answers);
    }

    private void saveApplicantAvailabilities(Application application, List<LocalDateTime> availableTimes) {
        List<ApplicantAvailability> availabilities = availableTimes.stream()
                .map(time -> ApplicantAvailability.of(application, time))
                .toList();
        applicantAvailabilityRepository.saveAll(availabilities);
    }

    // 요청과 실제 입력 파일 간의 검증 (이름, 갯수)
    private void validateFileAnswers(
            Recruitment recruitment,
            List<ApplicationAnswerRequestDTO> answers,
            List<MultipartFile> files
    ) {
        List<DocumentQuestion> questions = documentQuestionRepository.findByRecruitment(recruitment);
        Map<Long, DocumentQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(DocumentQuestion::getId, q -> q));

        Set<String> providedFileNames = files.stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toSet());

        for (ApplicationAnswerRequestDTO answer : answers) {
            DocumentQuestion question = questionMap.get(answer.questionId());

            if (question.getType() == QuestionType.FILE) {
                if (answer.fileName() == null || !providedFileNames.contains(answer.fileName())) {
                    throw new CustomException(ErrorCode.FILE_NAME_NOT_MATCH);
                }
            }
        }

        long expectedFileCount = answers.stream()
                .filter(a -> {
                    DocumentQuestion question = questionMap.get(a.questionId());
                    return question.getType() == QuestionType.FILE;
                })
                .count();

        if (files.size() != expectedFileCount) {
            throw new CustomException(ErrorCode.FILE_COUNT_MISMATCH);
        }
    }

}
