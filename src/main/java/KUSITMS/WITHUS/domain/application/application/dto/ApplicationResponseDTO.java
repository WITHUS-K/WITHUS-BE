package KUSITMS.WITHUS.domain.application.application.dto;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.availability.entity.ApplicantAvailability;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "지원서 응답 DTO")
public class ApplicationResponseDTO {

    @Schema(description = "지원서 상세 조회 응답 DTO")
    public record Detail(
            @Schema(description = "지원서 ID") Long id,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "성별") Gender gender,
            @Schema(description = "이메일") String email,
            @Schema(description = "전화번호") String phoneNumber,
            @Schema(description = "대학명") String university,
            @Schema(description = "전공") String major,
            @Schema(description = "생년월일") LocalDate birthDate,
            @Schema(description = "이미지 URL") String imageUrl,
            @Schema(description = "상태") ApplicationStatus status,
            @Schema(description = "면접 가능 시간") List<LocalDateTime> availableTimes
            ) {
        public static Detail from(Application application, List<ApplicantAvailability> availabilityList) {
            List<LocalDateTime> times = availabilityList.stream()
                    .map(ApplicantAvailability::getAvailableTime)
                    .toList();

            return new Detail(
                    application.getId(),
                    application.getName(),
                    application.getGender(),
                    application.getEmail(),
                    application.getPhoneNumber(),
                    application.getUniversity(),
                    application.getMajor(),
                    application.getBirthDate(),
                    application.getImageUrl(),
                    application.getStatus(),
                    times
            );
        }
    }

    @Schema(description = "지원서 요약 응답 DTO")
    public record Summary(
            @Schema(description = "지원서 ID") Long id,
            @Schema(description = "지원자 이름") String name,
            @Schema(description = "이메일") String email,
            @Schema(description = "파트명") String positionName,
            @Schema(description = "상태") ApplicationStatus status
    ) {
        public static Summary from(Application application) {
            return new Summary(
                    application.getId(),
                    application.getName(),
                    application.getEmail(),
                    application.getPosition().getName(),
                    application.getStatus()
            );
        }
    }
}
