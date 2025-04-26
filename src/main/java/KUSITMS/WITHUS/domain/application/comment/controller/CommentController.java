package KUSITMS.WITHUS.domain.application.comment.controller;

import KUSITMS.WITHUS.domain.application.comment.dto.CommentRequestDTO;
import KUSITMS.WITHUS.domain.application.comment.dto.CommentResponseDTO;
import KUSITMS.WITHUS.domain.application.comment.service.CommentService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.annotation.CurrentUser;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "코맨트 Controller")
@RequestMapping("/api/v1/applications/{applicationId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "지원서에 코멘트 추가", description = "운영진이 특정 지원서에 코멘트를 추가합니다.")
    public SuccessResponse<CommentResponseDTO.Summary> addComment(
            @PathVariable Long applicationId,
            @RequestBody @Valid CommentRequestDTO.Create request,
            @CurrentUser User currentUser
    ) {
        return SuccessResponse.ok(commentService.addComment(applicationId, currentUser.getId(), request));
    }
}
