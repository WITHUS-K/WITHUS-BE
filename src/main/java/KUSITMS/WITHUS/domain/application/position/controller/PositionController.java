package KUSITMS.WITHUS.domain.application.position.controller;

import KUSITMS.WITHUS.domain.application.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.application.position.dto.PositionResponseDTO;
import KUSITMS.WITHUS.domain.application.position.service.PositionService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "파트 Controller")
@RequestMapping("api/v1/positions")
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    @Operation(summary = "파트 생성", description = "조직 ID를 기반으로 파트를 생성합니다.")
    public SuccessResponse<PositionResponseDTO.Detail> create(
            @Valid @RequestBody PositionRequestDTO.Create request
    ) {
        return SuccessResponse.ok(positionService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "파트 삭제", description = "해당 ID의 파트를 삭제합니다.")
    public SuccessResponse<String> delete(@PathVariable Long id) {
        positionService.delete(id);
        return SuccessResponse.ok("파트 생성에 성공하였습니다.");
    }
}
