package KUSITMS.WITHUS.domain.application.application.service;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
    ApplicationResponseDTO.Summary create(ApplicationRequestDTO.Create request, MultipartFile profileImage, List<MultipartFile> files);
    void delete(Long id);
    ApplicationResponseDTO.Detail getById(Long id);
    List<ApplicationResponseDTO.Summary> getByRecruitmentId(Long recruitmentId);
    void updateStatus(ApplicationRequestDTO.UpdateStatus request);
}
