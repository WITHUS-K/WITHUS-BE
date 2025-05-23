package KUSITMS.WITHUS.domain.recruitment.recruitment.service;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.user.user.entity.User;

import java.util.List;
import java.util.function.Function;

public interface RecruitmentService {
    RecruitmentResponseDTO.Create saveDraft(RecruitmentRequestDTO.Upsert request);
    RecruitmentResponseDTO.Create publish(RecruitmentRequestDTO.Upsert request);
    RecruitmentResponseDTO.Detail getById(Long id);
    <R> R getByIdAs(Long id, Function<Recruitment,R> mapper);
    RecruitmentResponseDTO.Update update(Long id, RecruitmentRequestDTO.Update request);
    void delete(Long id);
    List<RecruitmentResponseDTO.Summary> getAllByKeyword(String keyword);
    RecruitmentResponseDTO.Detail getBySlug(String slug);
    List<RecruitmentResponseDTO.Simple> getAllByUserOrganizations(User user);
    List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForUser(Long userId, Long organizationId);
    List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForAdmin(Long adminUserId);
    List<RecruitmentResponseDTO.TaskProgress> getTaskProgress(Long recruitmentId, EvaluationType stage);
    RecruitmentResponseDTO.PendingEvaluator getPendingEvaluators(Long recruitmentId);
    RecruitmentResponseDTO.MyDocumentEvaluation getMyDocumentEvaluations(Long userId, Long recruitmentId);
}
