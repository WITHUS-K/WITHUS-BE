package KUSITMS.WITHUS.domain.interview.timeslot.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationResponseDTO;
import KUSITMS.WITHUS.domain.interview.timeslot.service.TimeSlotService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-slots")
@Tag(name = "면접 TimeSlot 관련 API")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping("/{id}/applications")
    @Operation(summary = "타임 슬롯에 배정된 지원자 조회", description = "해당 타임 슬롯 ID에 배정된 모든 지원자의 상세 정보를 조회합니다.")
    public SuccessResponse<List<ApplicationResponseDTO.DetailForTimeSlot>> getApplicationsByTimeSlot(@PathVariable Long id, @CurrentUser User user) {
        return SuccessResponse.ok(timeSlotService.getApplicationsByTimeSlotFilteredByUser(id, user.getId()));
    }
}
