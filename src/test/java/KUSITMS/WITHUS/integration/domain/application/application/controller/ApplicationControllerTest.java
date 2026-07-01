package KUSITMS.WITHUS.integration.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerRequestDTO;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import KUSITMS.WITHUS.integration.util.TestAuthHelper;
import KUSITMS.WITHUS.integration.util.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class ApplicationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private TestAuthHelper testAuthHelper;
    @Autowired private TestHelper testHelper;
    @Autowired private UserRepository userRepository;
    @Autowired private OrganizationService organizationService;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private RecruitmentRepository recruitmentRepository;
    @Autowired private EntityManager entityManager;

    private final String testMail = "testMail@gmail.com";
    private Long savedOrganizationId;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        savedOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("테스트 조직")).id();
        createTestUser();
        accessToken = testAuthHelper.loginAndGetAccessToken(testMail, "password1!");
    }

    private void createTestUser() {
        String testPhone = "01000001111";
        var user = User.builder()
                .name("테스트유저")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email(testMail)
                .phoneNumber(testPhone)
                .password(encoder.encode("password1!"))
                .build();
        userRepository.save(user);
    }

    private void addRolesToRecruitment(Long recruitmentId, String title, List<Long> organizationRoleIds) throws Exception {
        var updateRequest = new RecruitmentRequestDTO.Update(
                title, "설명", null,
                organizationRoleIds,
                LocalDate.now().plusDays(5), true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, false, true, true, true, true, false, true, false,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(), true, List.of()
        );

        mockMvc.perform(put("/api/v1/recruitments/" + recruitmentId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    private MockMultipartFile applicationRequestPart(Long recruitmentId, Long organizationRoleId, String email) throws Exception {
        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "역할검증", email, "01012341234", Gender.MALE,
                "상명대학교", "컴퓨터공학과", AcademicStatus.ENROLLED,
                LocalDate.of(2001, 1, 1), "서울시 도봉구 56로 501",
                recruitmentId,
                organizationRoleId,
                List.of(),
                List.of(LocalDateTime.of(2025, 4, 22, 10, 0))
        );

        return new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );
    }

    @Test
    @DisplayName("지원서 생성 성공")
    void createApplicationSuccess() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("지원서 테스트용 공고", savedOrganizationId, accessToken);
        Long organizationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        
        // 공고에 역할 추가
        var updateRequest = new RecruitmentRequestDTO.Update(
                "지원서 테스트용 공고", "설명", null,
                List.of(organizationRoleId),
                LocalDate.now().plusDays(5), true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, false, true, true, true, true, false, true, false,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(), true, List.of()
        );
        mockMvc.perform(put("/api/v1/recruitments/" + recruitmentId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "김재관", "test@example.com", "01012341234", Gender.MALE,
                "상명대학교", "컴퓨터공학과", AcademicStatus.ENROLLED,
                LocalDate.of(2001, 1, 1), "서울시 도봉구 56로 501",
                recruitmentId,
                organizationRoleId,
                List.of(),
                List.of(LocalDateTime.of(2025, 4, 22, 10, 0))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );

        mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("김재관"))
                .andExpect(jsonPath("$.result.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.organizationRoleName").value("백엔드"))
                .andExpect(jsonPath("$.result.status").value(ApplicationStatus.PENDING.name()));
    }

    @Test
    @DisplayName("지원서 생성 실패 - 필수값 누락")
    void createApplication_missingRequiredField_shouldReturnBadRequest() throws Exception {
        ApplicationRequestDTO.Create invalidRequest = new ApplicationRequestDTO.Create(
                "",
                "invalid-email",
                "",
                null,
                "대학", "전공", AcademicStatus.ENROLLED,
                LocalDate.of(2000, 1, 1), "주소",
                null,
                null,
                List.of(),
                List.of(LocalDateTime.of(2025, 4, 22, 10, 0))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(invalidRequest)
        );

        mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    @DisplayName("지원서 생성 실패 - 공고에 포함되지 않은 조직 역할")
    void createApplicationWithRoleNotIncludedInRecruitmentShouldReturnNotFound() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("역할 검증 공고", savedOrganizationId, accessToken);
        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long frontendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "프론트엔드", accessToken);
        addRolesToRecruitment(recruitmentId, "역할 검증 공고", List.of(backendRoleId));

        mockMvc.perform(multipart("/api/v1/applications")
                        .file(applicationRequestPart(recruitmentId, frontendRoleId, "not-included-role@example.com"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("지원서 생성 실패 - 다른 조직의 역할")
    void createApplicationWithOtherOrganizationRoleShouldReturnNotFound() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("다른 조직 역할 검증 공고", savedOrganizationId, accessToken);
        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        addRolesToRecruitment(recruitmentId, "다른 조직 역할 검증 공고", List.of(backendRoleId));

        Long otherOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("다른 테스트 조직")).id();
        Long otherOrganizationRoleId = testHelper.createOrganizationRole(otherOrganizationId, "디자인", accessToken);

        mockMvc.perform(multipart("/api/v1/applications")
                        .file(applicationRequestPart(recruitmentId, otherOrganizationRoleId, "other-org-role@example.com"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("지원서 단건 조회 성공")
    void getApplicationByIdSuccess() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("조회용 공고", savedOrganizationId, accessToken);
        Long organizationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "프론트엔드", accessToken);
        
        // 공고에 역할 추가
        var updateRequest = new RecruitmentRequestDTO.Update(
                "조회용 공고", "설명", null,
                List.of(organizationRoleId),
                LocalDate.now().plusDays(5), true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, false, true, true, true, true, false, true, false,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(), true, List.of()
        );
        mockMvc.perform(put("/api/v1/recruitments/" + recruitmentId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        Long appId = testHelper.createApplication(
                accessToken,
                recruitmentId,
                organizationRoleId,
                "홍길동",
                "user@example.com"
        );

        mockMvc.perform(get("/api/v1/applications/" + appId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("홍길동"))
                .andExpect(jsonPath("$.result.email").value("user@example.com"));
    }

    @Test
    @DisplayName("지원서 생성 성공 - 공통 질문과 역할별 질문 답변 저장")
    void createApplicationSavesCommonAndRoleQuestionAnswers() throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(savedOrganizationId, "디자인", accessToken);
        var recruitmentRequest = new RecruitmentRequestDTO.Upsert(
                null, "질문 답변 저장 공고", "설명",
                List.of(backendRoleId, designRoleId),
                List.of(
                        new DocumentQuestionRequestDTO.Create("공통 자기소개", "", QuestionType.TEXT, true, 500, true, null, null, null, 1),
                        new DocumentQuestionRequestDTO.Create("백엔드 경험", "", QuestionType.TEXT, true, 500, true, null, null, backendRoleId, 2),
                        new DocumentQuestionRequestDTO.Create("디자인 경험", "", QuestionType.TEXT, true, 500, true, null, null, designRoleId, 3)
                ),
                LocalDate.now().plusDays(5), true,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, savedOrganizationId,
                false, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(),
                true,
                List.of(new AvailableTimeRangeRequestDTO(LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(18, 0)))
        );

        String recruitmentResponse = mockMvc.perform(post("/api/v1/recruitments/publish")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recruitmentRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long recruitmentId = ((Number) JsonPath.read(recruitmentResponse, "$.result.recruitmentId")).longValue();
        entityManager.flush();
        entityManager.clear();

        Long commonQuestionId = recruitmentRepository.getById(recruitmentId).getQuestions().stream()
                .filter(question -> question.getTitle().equals("공통 자기소개"))
                .findFirst()
                .orElseThrow()
                .getId();
        Long backendQuestionId = recruitmentRepository.getById(recruitmentId).getQuestions().stream()
                .filter(question -> question.getTitle().equals("백엔드 경험"))
                .findFirst()
                .orElseThrow()
                .getId();

        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "답변지원자", "answer@example.com", "01012341234", Gender.MALE,
                "상명대학교", "컴퓨터공학과", AcademicStatus.ENROLLED,
                LocalDate.of(2001, 1, 1), "서울시",
                recruitmentId,
                backendRoleId,
                List.of(
                        new ApplicationAnswerRequestDTO(commonQuestionId, "공통 답변입니다.", null),
                        new ApplicationAnswerRequestDTO(backendQuestionId, "백엔드 답변입니다.", null)
                ),
                List.of(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );

        String applicationResponse = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.organizationRoleName").value("백엔드"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long applicationId = ((Number) JsonPath.read(applicationResponse, "$.result.id")).longValue();

        entityManager.flush();
        entityManager.clear();

        List<String> savedAnswerTexts = entityManager.createQuery(
                        "select answer.answerText from ApplicationAnswer answer " +
                                "where answer.application.id = :applicationId order by answer.id",
                        String.class
                )
                .setParameter("applicationId", applicationId)
                .getResultList();

        assertThat(savedAnswerTexts).containsExactly("공통 답변입니다.", "백엔드 답변입니다.");
    }

    @Test
    @DisplayName("지원서 단건 조회 실패 - 존재하지 않는 ID")
    void getApplication_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/applications/999999")
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("지원서 지인 여부 토글 성공")
    void toggleAcquaintanceSuccess() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("지인 테스트 공고", savedOrganizationId, accessToken);
        Long organizationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "기획", accessToken);
        
        // 공고에 역할 추가
        var updateRequest = new RecruitmentRequestDTO.Update(
                "지인 테스트 공고", "설명", null,
                List.of(organizationRoleId),
                LocalDate.now().plusDays(5), true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, false, true, true, true, true, false, true, false,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(), true, List.of()
        );
        mockMvc.perform(put("/api/v1/recruitments/" + recruitmentId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        Long appId = testHelper.createApplication(
                accessToken,
                recruitmentId,
                organizationRoleId,
                "박토글",
                "toggle@example.com"
        );

        mockMvc.perform(patch("/api/v1/applications/" + appId + "/acquaintance")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.acquainted").isBoolean());
    }

    @Test
    @DisplayName("서류 평가 담당자의 공고별 평가 대상 지원서 목록 조회 성공")
    void getApplicationsByRecruitmentForEvaluator() throws Exception {
        // Given
        Long recruitmentId = testHelper.createRecruitment("서류 평가 공고", savedOrganizationId, accessToken);
        Long organizationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        
        // 공고에 역할 추가
        var updateRequest = new RecruitmentRequestDTO.Update(
                "서류 평가 공고", "설명", null,
                List.of(organizationRoleId),
                LocalDate.now().plusDays(5), true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, false, true, true, true, true, false, true, false,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(), true, List.of()
        );
        mockMvc.perform(put("/api/v1/recruitments/" + recruitmentId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        Long applicationId = testHelper.createApplication(
                accessToken,
                recruitmentId,
                organizationRoleId,
                "김김김",
                "eval@example.com"
        );

        Long evaluatorId = userRepository.getByEmail(testMail).getId();

        // When
        var assignRequest = new ApplicationEvaluatorRequestDTO.Update(
                applicationId,
                EvaluationType.DOCUMENT,
                List.of(evaluatorId)
        );

        mockMvc.perform(post("/api/v1/admin/applications/evaluators")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk());

        // Then
        mockMvc.perform(get("/api/v1/applications/recruitment/" + recruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data[0].name").value("김김김"))
                .andExpect(jsonPath("$.result.data[0].organizationRoleName").value("백엔드"))
                .andExpect(jsonPath("$.result.data[0].status").value(ApplicationStatus.PENDING.name()));
    }

}
