package KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.service;

import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaResponseDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.entity.EvaluationCriteria;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluationCriteriaServiceImpl implements EvaluationCriteriaService {

    private final RecruitmentRepository recruitmentRepository;
    private final PositionRepository positionRepository;

    /**
     * Recruitment에 평가 기준 추가
     * @param recruitmentId 추가할 공고 ID
     * @param request 평가 기준, 평가 타입(서류, 면접)을 받는 DTO
     * @return 생성된 평가 기준 정보 반환
     */
    @Override
    @Transactional
    public EvaluationCriteriaResponseDTO.Create addCriteria(Long recruitmentId, EvaluationCriteriaRequestDTO.Create request) {
        Recruitment recruitment = recruitmentRepository.getById(recruitmentId);
        Position position = getPositionIfExists(request.positionId());

        EvaluationCriteria criteria = EvaluationCriteria.builder()
                .content(request.content())
                .evaluationType(request.type())
                .position(position)
                .build();

        recruitment.addEvaluationCriteria(criteria);
        return EvaluationCriteriaResponseDTO.Create.from(criteria);
    }

    private Position getPositionIfExists(Long positionId) {
        if (positionId == null) return null;
        return positionRepository.getById(positionId);
    }
}
