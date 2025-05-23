package KUSITMS.WITHUS.domain.application.application.dto;

import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminStageFilter;
import KUSITMS.WITHUS.domain.application.application.enumerate.SimpleApplicationStatus;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerRequestDTO;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "지원서 요청 DTO")
public class ApplicationRequestDTO {

    @Schema(description = "지원서 생성 요청 DTO")
    public record Create(
            @Schema(description = "지원자 이름", example = "김김김")
            @NotBlank String name,

            @Schema(description = "이메일", example = "test@example.com")
            @NotBlank @Email String email,

            @Schema(description = "전화번호", example = "01012341234")
            @NotBlank String phoneNumber,

            @Schema(description = "성별", example = "MALE")
            @NotNull Gender gender,

            @Schema(description = "대학명", example = "상명대학교")
            String university,

            @Schema(description = "전공명", example = "컴퓨터공학과")
            String major,

            @Schema(description = "학적 상태", example = "ENROLLED | GRADUATED | LEAVE_OF_ABSENCE | DEFERRED")
            AcademicStatus academicStatus,

            @Schema(description = "생년월일", example = "2000-01-01")
            LocalDate birthDate,

            @Schema(description = "주소", example = "서울시 도봉구 56로 501")
            String address,

            @Schema(description = "공고 ID", example = "1")
            @NotNull Long recruitmentId,

            @Schema(description = "파트 ID", example = "1")
            Long positionId,

            @Schema(description = "지원서 질문 답변 목록")
            List<ApplicationAnswerRequestDTO> answers,

            @Schema(description = "면접 가능 시간", example = "[\"2025-04-22T10:00:00\", \"2025-04-23T14:30:00\"]")
            @NotNull List<LocalDateTime> availableTimes
    ) {}

    @Schema(description = "지원서 상태 일괄 수정 요청 DTO")
    public record UpdateStatus(
            @Schema(description = "지원서 ID 리스트", example = "[1, 2, 3]")
            @NotEmpty List<Long> applicationIds,

            @Schema(description = "어떤 단계의 상태를 바꿀지 (DOCUMENT/INTERVIEW)", example = "DOCUMENT")
            @NotNull AdminStageFilter stage,

            @Schema(description = "변경할 간단 상태 (PASS/FAIL/HOLD)", example = "PASS")
            @NotNull SimpleApplicationStatus status
    ) {}

    @Schema(description = "여러 사용자에게 메일 일괄 발송 요청 DTO")
    public record BulkMailRequest(
            @Schema(description = "수신자 지원자(지원서) ID 리스트", example = "[1, 2, 3]")
            @NotEmpty List<@Positive Long> applicationIds,

            @Schema(description = "메일 제목", example = "[WITHUS] 공지사항")
            @NotBlank String subject,

            @Schema(description = "메일 본문 (HTML 가능)", example = "<p>안녕하세요!</p>")
            @NotBlank String body,

            @Schema(description = "첨부파일 리스트 (선택)")
            List<MultipartFile> attachments
    ) {}

    @Schema(description = "여러 사용자에게 문자 일괄 발송 요청 DTO")
    public record BulkSmsRequest(
            @Schema(description = "수신자 지원자(지원서) ID 리스트", example = "[1, 2, 3]")
            @NotEmpty List<@Positive Long> applicationIds,

            @Schema(description = "문자 메시지 본문", example = "안녕하세요, 테스트 문자입니다.")
            @NotBlank String message,

            @Schema(description = "첨부파일 (선택, MMS 이미지)", nullable = true)
            MultipartFile attachment
    ) {}
}
