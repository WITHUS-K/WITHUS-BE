package KUSITMS.WITHUS.domain.user.userOrganization.service;

import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserOrganizationService {
    Page<UserResponseDTO.DetailForOrganization> getUsers(Long organizationId, int page, int size);
    UserOrganization addUserToOrganization(Long organizationId, Long userId);
    void removeUsers(Long organizationId, List<Long> userIds);
    List<UserResponseDTO.Summary> getAllUsersByOrganization(Long organizationId, String keyword);
}
