package KUSITMS.WITHUS.domain.recruitment.documentQuestion.service;

import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.entity.DocumentQuestion;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.repository.DocumentQuestionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DocumentQuestionServiceImpl implements DocumentQuestionService{

    private final DocumentQuestionRepository documentQuestionRepository;
    private final RecruitmentRepository recruitmentRepository;


    /**
     * 지원서 서류 질문 생성
     * @param request 생성 DTO
     * @return 생성된 질문 요약 정보 반환
     */
    @Transactional
    public DocumentQuestionResponseDTO.Create create(DocumentQuestionRequestDTO.Create request) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());

        DocumentQuestion question = DocumentQuestion.builder()
                .recruitment(recruitment)
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .required(request.required())
                .textLimit(request.textLimit())
                .includeWhitespace(request.includeWhitespace())
                .maxFileCount(request.maxFileCount())
                .maxFileSizeMb(request.maxFileSizeMb())
                .build();

        DocumentQuestion savedQuestion = documentQuestionRepository.save(question);

        return DocumentQuestionResponseDTO.Create.from(savedQuestion);
    }
}
