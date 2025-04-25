package KUSITMS.WITHUS.domain.evaluation.evaluation.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluation.entity.Evaluation;
import KUSITMS.WITHUS.domain.evaluation.evaluation.repository.EvaluationRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.repository.EvaluationCriteriaRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ApplicationRepository applicationRepository;
    private final EvaluationCriteriaRepository criteriaRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EvaluationResponseDTO.Detail evaluate(EvaluationRequestDTO.Create request, Long userId) {
        if (evaluationRepository.existsByApplicationAndCriteriaAndUser(
                request.applicationId(), request.criteriaId(), userId)) {
            throw new CustomException(ErrorCode.EVALUATION_ALREADY_EXIST);
        }

        Application application = applicationRepository.getById(request.applicationId());
        EvaluationCriteria criteria = criteriaRepository.getById(request.criteriaId());
        User user = userRepository.getById(userId);

        Evaluation evaluation = Evaluation.builder()
                .score(request.score())
                .application(application)
                .criteria(criteria)
                .user(user)
                .build();

        application.addEvaluation(evaluation);
        criteria.addEvaluation(evaluation);

        evaluationRepository.save(evaluation);
        return EvaluationResponseDTO.Detail.from(evaluation);
    }
}
