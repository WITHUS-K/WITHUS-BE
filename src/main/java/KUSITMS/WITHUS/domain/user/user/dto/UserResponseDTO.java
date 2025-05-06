package KUSITMS.WITHUS.domain.user.user.dto;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.global.common.annotation.DateFormat;
import KUSITMS.WITHUS.global.common.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "사용자 관련 응답 DTO")
public class UserResponseDTO {

    @Schema(description = "사용자 요약 정보 응답 DTO")
    public record Summary(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name
    ) {
        public static Summary from(User user) {
            return new Summary(
                    user.getId(),
                    user.getName()
            );
        }
    }

    @Schema(description = "검색 결과에 대한 사용자 요약 정보 응답 DTO")
    public record SummaryForSearch(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "이메일") String email,
            @Schema(description = "프로필 사진 URL") String imageUrl
    ) {
        public static SummaryForSearch from(User user) {
            return new SummaryForSearch(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getProfileImageUrl()
            );
        }
    }

    @Schema(description = "사용자 상세 정보 응답 DTO")
    public record DetailProfile(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "전화번호") String phoneNumber,
            @Schema(description = "이메일") String email,
            @Schema(description = "생년월일") @DateFormat LocalDate birthDate,
            @Schema(description = "성별") String gender,
            @Schema(description = "역할") List<OrganizationRoleResponseDTO.Detail> roles
    ) {
        public static DetailProfile from(User user, Long organizationId) {
            return new DetailProfile(
                    user.getId(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getEmail(),
                    user.getBirthDate(),
                    user.getGender().getKey(),
                    user.getUserOrganizationRoles().stream()
                            .filter(userOrganizationRole ->
                                    userOrganizationRole.getOrganizationRole()
                                            .getOrganization()
                                            .getId()
                                            .equals(organizationId)
                            )
                            .map(userOrganizationRole ->
                                    OrganizationRoleResponseDTO.Detail.from(userOrganizationRole.getOrganizationRole())
                            )
                            .toList()
            );
        }
    }

    @Schema(description = "TimeSlot에서 사용할 사용자 요약 정보 응답 DTO")
    public record SummaryForTimeSlot(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "역할") InterviewRole role
    ) {
        public static SummaryForTimeSlot from(User user, InterviewRole role) {
            return new SummaryForTimeSlot(
                    user.getId(),
                    user.getName(),
                    role
            );
        }
    }

    @Schema(description = "TimeSlot에서 사용할 사용자 요약 정보 응답 DTO")
    public record DetailForOrganization(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "역할") Role role,
            @Schema(description = "이메일") String email,
            @Schema(description = "전화번호") String phoneNumber,
            @Schema(description = "생성일시") @DateTimeFormat LocalDateTime createdAt
    ) {
        public static DetailForOrganization from(User user) {
            return new DetailForOrganization(
                    user.getId(),
                    user.getName(),
                    user.getRole(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getCreatedAt()
            );
        }
    }

    @Schema(description = "이메일 중복 확인 응답 DTO")
    public record EmailDuplicateCheck (
            @Schema(description = "이메일 중복 여부", example = "false") boolean isDuplicated
    ) {}
}
