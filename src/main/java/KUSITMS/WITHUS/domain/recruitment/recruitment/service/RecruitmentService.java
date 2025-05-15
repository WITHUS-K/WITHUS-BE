package KUSITMS.WITHUS.domain.recruitment.recruitment.service;

import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;

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
    List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForUser(Long userId, Long organizationId);
    List<RecruitmentResponseDTO.SummaryForHome> getCurrentSummariesForAdmin(Long adminUserId);
}
