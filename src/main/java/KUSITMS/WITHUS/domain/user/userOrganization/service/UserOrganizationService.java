package KUSITMS.WITHUS.domain.user.userOrganization.service;

import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserOrganizationService {
    Page<UserResponseDTO.DetailProfile> getUsers(Long organizationId, int page, int size);
    List<UserOrganizationResponseDTO.Detail> addUserToOrganization(Long organizationId, List<Long> userIds);
    void removeUsers(Long organizationId, List<Long> userIds);
    List<UserResponseDTO.SummaryForSearch> getUsersWithAssignment(Long organizationId, String keyword, Long roleId);
    void sendInvitationEmails(Long organizationId, List<Long> userIds, String inviterName);
}
