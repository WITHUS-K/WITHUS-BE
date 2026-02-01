package KUSITMS.WITHUS.domain.interview.timeslot.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.dto.TimeSlotRequestDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.dto.TimeSlotResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.service.TimeSlotService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-slots")
@Tag(name = "면접 TimeSlot 관련 API")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping("/{timeSlotId}/applications")
    @Operation(summary = "타임 슬롯에 배정된 지원자 조회", description = "해당 타임 슬롯 ID에 배정된 모든 지원자의 상세 정보를 조회합니다.")
    public SuccessResponse<List<ApplicationResponseDTO.DetailForTimeSlot>> getApplicationsByTimeSlot(@PathVariable Long timeSlotId, @CurrentUser User user) {
        return SuccessResponse.ok(timeSlotService.getApplicationsByTimeSlotFilteredByUser(timeSlotId, user.getId()));
    }

    @GetMapping("/{timeSlotId}")
    @Operation(summary = "타임슬롯 조회", description = "특정 타임슬롯에 배정된 사용자/지원자 목록을 조회합니다.")
    public SuccessResponse<TimeSlotResponseDTO.Detail> getUsersByTimeSlot(
            @PathVariable Long timeSlotId
    ) {
        return SuccessResponse.ok(timeSlotService.getTimeSlotDetail(timeSlotId));
    }

    @PostMapping("/{timeSlotId}/applications")
    @Operation(summary = "타임 슬롯에 지원자 추가", description = "특정 타임 슬롯에 수동으로 지원자를 추가합니다.")
    public SuccessResponse<String> addApplicantToTimeSlot(
            @PathVariable Long timeSlotId,
            @RequestBody TimeSlotRequestDTO.UpsertApplicant request
    ) {
        timeSlotService.addApplicantToTimeSlot(timeSlotId, request.applicantIds());
        return SuccessResponse.ok("지원자가 타임슬롯에 추가되었습니다.");
    }

    @PatchMapping("/{timeSlotId}/applications")
    @Operation(summary = "타임 슬롯 지원자 수정", description = "특정 타임 슬롯에 배정된 지원자의 정보를 수정합니다.")
    public SuccessResponse<String> updateApplicantInTimeSlot(
            @PathVariable Long timeSlotId,
            @RequestBody TimeSlotRequestDTO.UpsertApplicant request
    ) {
        timeSlotService.updateApplicantInTimeSlot(timeSlotId, request.applicantIds());
        return SuccessResponse.ok("지원자 정보가 수정되었습니다.");
    }
}
