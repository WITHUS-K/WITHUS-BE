package KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.controller;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import KUSITMS.WITHUS.domain.interview.interview.service.InterviewService;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.dto.InterviewerAvailabilityRequestDTO;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.dto.InterviewerAvailabilityResponseDTO;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.entity.InterviewerAvailability;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.service.InterviewAvailabilityMailService;
import KUSITMS.WITHUS.domain.interview.interviewAvailabiliy.service.InterviewerAvailabilityService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviewers")
@Tag(name = "운영진 면접 가능 시간 API")
@RequiredArgsConstructor
public class InterviewerAvailabilityController {

    private final InterviewerAvailabilityService availabilityService;
    private final InterviewService interviewService;
    private final InterviewAvailabilityMailService mailService;

    @PostMapping("/{interviewId}/availabilities")
    @Operation(summary = "면접 가능 시간 등록", description = "운영진이 면접 가능한 시간을 리스트로 등록합니다.")
    public SuccessResponse<List<InterviewerAvailabilityResponseDTO>> registerAvailability(
            @PathVariable Long interviewId,
            @Valid @RequestBody InterviewerAvailabilityRequestDTO request,
            @CurrentUser User user
    ) {
        List<InterviewerAvailability> availabilities = availabilityService.registerAvailability(interviewId, user, request.availableTimes());
        List<InterviewerAvailabilityResponseDTO> response = availabilities.stream()
                .map(InterviewerAvailabilityResponseDTO::from)
                .toList();
        return SuccessResponse.ok(response);
    }

    @PostMapping("/{interviewId}/availabilities/request-mail")
    @Operation(summary = "운영진 면접 가능 시간 입력 요청 메일 전송", description = "조직 운영진들에게 면접 가능 시간 입력을 요청하는 이메일을 발송합니다.")
    public SuccessResponse<String> requestAvailabilityInputMail(
            @PathVariable Long interviewId
    ) {
        Interview interview = interviewService.getById(interviewId);
        Long organizationId = interview.getRecruitment().getOrganization().getId();

        mailService.sendMailToUsers(organizationId);

        return SuccessResponse.ok("면접 일정 요청 메일 전송에 성공하였습니다.");
    }
}
