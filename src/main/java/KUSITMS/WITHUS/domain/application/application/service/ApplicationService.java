package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminApplicationSortField;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminStageFilter;
import KUSITMS.WITHUS.domain.application.application.enumerate.EvaluationStatus;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDTO.Summary create(ApplicationRequestDTO.Create request, MultipartFile profileImage, List<MultipartFile> files);
    void delete(Long id);
    ApplicationResponseDTO.Detail getById(Long id, Long currentUserId);
    Page<ApplicationResponseDTO.SummaryForUser> getByRecruitmentId(Long recruitmentId, Long currentUserId, EvaluationStatus evaluationStatus, String keyword, Pageable pageable);
    Page<ApplicationResponseDTO.SummaryForAdmin> getByRecruitmentIdForAdmin(Long recruitmentId, AdminStageFilter stage, Pageable pageable, AdminApplicationSortField sortBy, Sort.Direction direction);
    List<ApplicationResponseDTO.Summary> updateStatus(ApplicationRequestDTO.UpdateStatus request);
    void distributeEvaluators(ApplicationEvaluatorRequestDTO.Distribute request);
    DistributionRequest distributeEvaluatorsLatestRequest(Long recruitmentId);
    void updateEvaluators(ApplicationEvaluatorRequestDTO.Update request);
    boolean toggleAcquaintance(Long applicationId, Long userId);
}
