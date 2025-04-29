package KUSITMS.WITHUS.domain.recruitment.controller;

import KUSITMS.WITHUS.domain.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.dto.RecruitmentResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.service.RecruitmentService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "공고(리크루팅) Controller")
@RequestMapping("/api/v1/recruitments")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @PostMapping
    @Operation(summary = "리크루팅 생성")
    public SuccessResponse<RecruitmentResponseDTO.Create> create(
            @Valid @RequestBody RecruitmentRequestDTO.Create request
    ) {
        RecruitmentResponseDTO.Create result = recruitmentService.create(request);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "리크루팅 단건 상세 조회")
    public SuccessResponse<RecruitmentResponseDTO.Detail> getById(@PathVariable Long id) {
        RecruitmentResponseDTO.Detail result = recruitmentService.getById(id);
        return SuccessResponse.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "리크루팅 수정")
    public SuccessResponse<RecruitmentResponseDTO.Update> update(
            @PathVariable Long id,
            @Valid @RequestBody RecruitmentRequestDTO.Update request
    ) {
        RecruitmentResponseDTO.Update result = recruitmentService.update(id, request);
        return SuccessResponse.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "리크루팅 삭제")
    public SuccessResponse<String> delete(@PathVariable Long id) {
        recruitmentService.delete(id);
        return SuccessResponse.ok("리크루팅 삭제에 성공하였습니다.");
    }

    @GetMapping
    @Operation(summary = "리크루팅 목록 조회 및 검색", description = "공고의 title을 기준으로 keyword를 검색합니다. keyword가 없으면 전체 조회합니다.")
    public SuccessResponse<List<RecruitmentResponseDTO.Summary>> getAllByKeyword(
            @RequestParam(required = false) String keyword
    ) {
        List<RecruitmentResponseDTO.Summary> result = recruitmentService.getAllByKeyword(keyword);
        return SuccessResponse.ok(result);
    }
}
