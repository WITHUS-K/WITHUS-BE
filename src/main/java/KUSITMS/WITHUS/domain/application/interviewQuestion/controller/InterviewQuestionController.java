package KUSITMS.WITHUS.domain.application.interviewQuestion.controller;

import KUSITMS.WITHUS.domain.application.interviewQuestion.dto.InterviewQuestionRequestDTO;
import KUSITMS.WITHUS.domain.application.interviewQuestion.dto.InterviewQuestionResponseDTO;
import KUSITMS.WITHUS.domain.application.interviewQuestion.entity.InterviewQuestion;
import KUSITMS.WITHUS.domain.application.interviewQuestion.service.InterviewQuestionService;
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
@Tag(name = "면접 질문 Controller")
@RequestMapping("/api/v1/applications/{applicationId}/questions")
public class InterviewQuestionController {

    private final InterviewQuestionService interviewQuestionService;

    @PostMapping
    @Operation(summary = "지원서에 면접 질문 추가", description = "지원서에 면접 질문을 추가하고 등록한 운영진을 기록합니다.")
    public SuccessResponse<InterviewQuestionResponseDTO.Create> addQuestion(
            @PathVariable Long applicationId,
            @CurrentUser User currentUser,
            @RequestBody @Valid InterviewQuestionRequestDTO.Create request
    ) {
        InterviewQuestion interviewQuestion = interviewQuestionService.addQuestionToApplication(applicationId, currentUser.getId(), request.content());
        return SuccessResponse.ok(InterviewQuestionResponseDTO.Create.from(interviewQuestion));
    }

    @PatchMapping("/{questionId}")
    @Operation(summary = "면접 질문 수정", description = "기존 면접 질문의 내용을 수정합니다.")
    public SuccessResponse<InterviewQuestionResponseDTO.Detail> updateQuestion(
            @PathVariable Long applicationId,
            @PathVariable Long questionId,
            @CurrentUser User currentUser,
            @RequestBody @Valid InterviewQuestionRequestDTO.Update request
    ) {
        InterviewQuestion updated = interviewQuestionService.updateQuestion(questionId, currentUser.getId(), request.content());
        return SuccessResponse.ok(InterviewQuestionResponseDTO.Detail.from(updated));
    }

}
