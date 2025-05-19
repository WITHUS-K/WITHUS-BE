package KUSITMS.WITHUS.domain.user.user.dto;

import KUSITMS.WITHUS.domain.interview.enumerate.InterviewRole;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.userOrganization.dto.UserOrganizationResponseDTO;
import KUSITMS.WITHUS.global.common.annotation.DateFormatDot;
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
            @Schema(description = "이름") String name,
            @Schema(description = "프로필 이미지 url") String profileImageUrl
    ) {
        public static Summary from(User user) {
            return new Summary(
                    user.getId(),
                    user.getName(),
                    user.getProfileImageUrl()
            );
        }
    }

    @Schema(description = "로그인 시 사용자 요약 정보 응답 DTO")
    public record Login(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "프로필 이미지 url") String profileImageUrl,
            @Schema(description = "사용자가 속한 조직 ID") List<UserOrganizationResponseDTO.Detail> userOrganizations,
            @Schema(description = "사용자 / 관리자 여부") Role role,
            @Schema(description = "조직 내 역할(학회장, 부학회장, 기획 등) 리스트") List<OrganizationRoleResponseDTO.Detail> userOrganizationRoles
    ) {
        public static Login from(User user) {

            List<UserOrganizationResponseDTO.Detail> organizations = user.getUserOrganizations().stream()
                    .map(UserOrganizationResponseDTO.Detail::from)
                    .toList();

            List<OrganizationRoleResponseDTO.Detail> organizationRoles = user.getUserOrganizationRoles().stream()
                    .map(uor -> OrganizationRoleResponseDTO.Detail.from(uor.getOrganizationRole()))
                    .toList();

            return new Login(
                    user.getId(),
                    user.getName(),
                    user.getProfileImageUrl(),
                    organizations,
                    user.getRole(),
                    organizationRoles
            );
        }
    }

    @Schema(description = "이메일 검색 결과에 대한 사용자 요약 정보 응답 DTO")
    public record SummaryForEmailSearch(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "이메일") String email,
            @Schema(description = "프로필 사진 URL") String imageUrl
    ) {
        public static SummaryForEmailSearch from(User user) {
            return new SummaryForEmailSearch(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getProfileImageUrl()
            );
        }
    }

    @Schema(description = "검색 결과에 대한 사용자 요약 정보 응답 DTO")
    public record SummaryForSearch(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "이메일") String email,
            @Schema(description = "프로필 사진 URL") String imageUrl,
            @Schema(description = "역할에 속해있는지 여부") boolean isAssigned
    ) {
        public static SummaryForSearch from(User user, boolean isAssigned) {
            return new SummaryForSearch(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getProfileImageUrl(),
                    isAssigned
            );
        }
    }

    @Schema(description = "사용자 상세 정보 응답 DTO")
    public record DetailProfile(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "프로필 이미지 URL") String profileImageUrl,
            @Schema(description = "이름") String name,
            @Schema(description = "이메일") String email,
            @Schema(description = "역할") List<OrganizationRoleResponseDTO.Detail> roles,
            @Schema(description = "성별") String gender,
            @Schema(description = "생년월일") @DateFormatDot LocalDate birthDate,
            @Schema(description = "전화번호") String phoneNumber,
            @Schema(description = "가입일자") @DateTimeFormat LocalDateTime createdAt
    ) {
        public static DetailProfile from(User user, Long organizationId) {
            return new DetailProfile(
                    user.getId(),
                    user.getProfileImageUrl(),
                    user.getName(),
                    user.getEmail(),
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
                            .toList(),
                    user.getGender().getKey(),
                    user.getBirthDate(),
                    user.getPhoneNumber(),
                    user.getCreatedAt()
            );
        }
    }

    @Schema(description = "TimeSlot에서 사용할 사용자 요약 정보 응답 DTO")
    public record SummaryForTimeSlot(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "프로필 사진 URL") String profileUrl,
            @Schema(description = "역할") InterviewRole role
    ) {
        public static SummaryForTimeSlot from(User user, InterviewRole role) {
            return new SummaryForTimeSlot(
                    user.getId(),
                    user.getName(),
                    user.getProfileImageUrl(),
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

    @Schema(description = "마이페이지 DTO")
    public record MyPage(
            @Schema(description = "사용자 ID") Long userId,
            @Schema(description = "이름") String name,
            @Schema(description = "전화번호") String phoneNumber,
            @Schema(description = "이메일") String email,
            @Schema(description = "프로필 이미지 URL") String imageUrl,
            @Schema(description = "가입 동아리") List<OrganizationResponseDTO.Summary> organizations
    ) {
        public static MyPage from(User user) {
            return new MyPage(
                    user.getId(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getEmail(),
                    user.getProfileImageUrl(),
                    user.getUserOrganizations().stream()
                            .map(userOrg -> OrganizationResponseDTO.Summary.from(userOrg.getOrganization()))
                            .toList()
            );
        }
    }
}
