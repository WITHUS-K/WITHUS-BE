package KUSITMS.WITHUS.domain.interview.timeslotUser.controller;

import KUSITMS.WITHUS.domain.interview.timeslotUser.dto.TimeSlotUserRequestDTO;
import KUSITMS.WITHUS.domain.interview.timeslotUser.service.TimeSlotUserService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/timeslots")
@Tag(name = "TimeSlot에 사용자 추가 API")
@RequiredArgsConstructor
public class TimeSlotUserController {

    private final TimeSlotUserService timeSlotUserService;

    @PostMapping("/{timeSlotId}/users")
    @Operation(summary = "타임슬롯에 사용자 추가", description = "면접관, 안내자를 타임슬롯에 추가합니다.")
    public SuccessResponse<String> addUsersToTimeSlot(
            @PathVariable Long timeSlotId,
            @RequestBody @Valid TimeSlotUserRequestDTO.AddUser request
    ) {
        timeSlotUserService.addUsersToTimeSlot(timeSlotId, request.userIds(), request.role());
        return SuccessResponse.ok("타임슬롯에 사용자가 추가되었습니다.");
    }
}
