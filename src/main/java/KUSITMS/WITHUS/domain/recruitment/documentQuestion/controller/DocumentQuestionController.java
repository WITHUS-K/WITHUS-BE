package KUSITMS.WITHUS.domain.recruitment.documentQuestion.controller;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.service.DocumentQuestionService;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "지원서 서류 질문 Controller")
@RequestMapping("api/v1/document-questions")
public class DocumentQuestionController {

    private final DocumentQuestionService documentQuestionService;

    @Operation(summary = "서류 질문 생성", description = "서류 질문을 생성합니다.")
    @PostMapping
    public SuccessResponse<DocumentQuestionResponseDTO.Create> createDocumentQuestion(
            @Valid @RequestBody DocumentQuestionRequestDTO.Create request
    ) {
        DocumentQuestionResponseDTO.Create response = documentQuestionService.create(request);
        return SuccessResponse.ok(response);
    }
}
