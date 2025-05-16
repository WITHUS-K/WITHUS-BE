package KUSITMS.WITHUS.domain.interview.interview.controller;

import KUSITMS.WITHUS.domain.interview.interview.dto.InterviewScheduleDTO;
import KUSITMS.WITHUS.domain.interview.interview.service.InterviewSchedulerService;
import KUSITMS.WITHUS.domain.interview.interview.service.InterviewService;
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
@RequestMapping("/api/v1/interviews")
@Tag(name = "면접 스케줄링 API")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewSchedulerService schedulerService;
    private final InterviewService interviewService;

    @PostMapping("/recruitments/{recruitmentId}/interviews")
    @Operation(summary = "면접 생성", description = "면접 객체를 생성하고 ID를 반환합니다.")
    public SuccessResponse<Long> createInterview(@PathVariable Long recruitmentId) {
        Long interviewId = interviewService.create(recruitmentId);
        return SuccessResponse.ok(interviewId);
    }

    @PostMapping("/recruitments/{recruitmentId}/interviews/{interviewId}/schedule")
    @Operation(summary = "면접 타임테이블 생성", description = "공고별 서류 합격자들에 대해 면접 시간을 배정합니다.")
    public SuccessResponse<String> assignSchedule(
            @PathVariable Long recruitmentId,
            @PathVariable Long interviewId,
            @Valid @RequestBody InterviewSchedulerService.InterviewConfig config
    ) {
        schedulerService.assignInterviewSlots(recruitmentId, interviewId, config);
        return SuccessResponse.ok("면접 타임테이블 생성에 성공하였습니다.");
    }

    @GetMapping("/{interviewId}/schedule")
    @Operation(summary = "면접 배정 결과 조회", description = "해당 인터뷰 ID에 배정된 타임슬롯과 지원자 목록을 조회합니다.")
    public SuccessResponse<List<InterviewScheduleDTO>> getSchedule(@PathVariable("interviewId") Long interviewId) {
        return SuccessResponse.ok(schedulerService.getInterviewSchedule(interviewId));
    }

    @GetMapping("/{interviewId}/my-time-slots")
    @Operation(summary = "내 면접 배정 결과 조회", description = "해당 인터뷰 ID에 배정된 본인의 정보를 조회합니다.")
    public SuccessResponse<List<InterviewScheduleDTO.MyInterviewTimeDTO>> getSchedule(
            @PathVariable("interviewId") Long interviewId,
            @CurrentUser User user
    ) {
        return SuccessResponse.ok(schedulerService.getMyInterviewTimes(interviewId, user));
    }

    @GetMapping("/my-organization-interviews")
    @Operation(summary = "내 조직의 면접 정보 조회", description = "현재 사용자가 속한 조직에서 생성한 면접 정보를 조회합니다.")
    public SuccessResponse<List<InterviewScheduleDTO.MyInterviewScheduleSummaryDTO>> getMyOrgInterviews(
            @CurrentUser User user
    ) {
        return SuccessResponse.ok(interviewService.getMyOrganizationInterviews(user));
    }

}
