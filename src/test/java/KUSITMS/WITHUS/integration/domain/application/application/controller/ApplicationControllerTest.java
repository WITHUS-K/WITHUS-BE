package KUSITMS.WITHUS.integration.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.application.applicationAnswer.dto.ApplicationAnswerRequestDTO;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
                null,
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
                null,
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
                null,
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
    @DisplayName("지원서 생성 성공 - 여러 역할 그룹에서 선택한 파트별 질문 답변 저장")
    void createApplicationWithMultipleRoleGroupsSavesSelectedRoleAnswers() throws Exception {
        Long generalGroupId = createOrganizationRoleGroup("일반 파트");
        Long executiveGroupId = createOrganizationRoleGroup("운영진 팀");

        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long frontendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "프론트엔드", accessToken);
        Long educationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "교육기획팀", accessToken);

        assignRolesToGroup(generalGroupId, List.of(backendRoleId, frontendRoleId));
        assignRolesToGroup(executiveGroupId, List.of(educationRoleId));

        var recruitmentRequest = new RecruitmentRequestDTO.Upsert(
                null, "다중 파트 공고", "설명",
                List.of(backendRoleId, frontendRoleId, educationRoleId),
                List.of(
                        new DocumentQuestionRequestDTO.Create("공통 자기소개", "", QuestionType.TEXT, true, 500, true, null, null, null, 1),
                        new DocumentQuestionRequestDTO.Create("백엔드 경험", "", QuestionType.TEXT, true, 500, true, null, null, backendRoleId, 2),
                        new DocumentQuestionRequestDTO.Create("프론트엔드 경험", "", QuestionType.TEXT, true, 500, true, null, null, frontendRoleId, 3),
                        new DocumentQuestionRequestDTO.Create("교육기획 경험", "", QuestionType.TEXT, true, 500, true, null, null, educationRoleId, 4)
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

        Long commonQuestionId = findQuestionId(recruitmentId, "공통 자기소개");
        Long backendQuestionId = findQuestionId(recruitmentId, "백엔드 경험");
        Long educationQuestionId = findQuestionId(recruitmentId, "교육기획 경험");

        Map<String, Object> request = baseApplicationRequest(recruitmentId, "multi-role@example.com");
        request.put("positionIds", List.of(educationRoleId, backendRoleId));
        request.put("answers", List.of(
                new ApplicationAnswerRequestDTO(commonQuestionId, "공통 답변입니다.", null),
                new ApplicationAnswerRequestDTO(backendQuestionId, "백엔드 답변입니다.", null),
                new ApplicationAnswerRequestDTO(educationQuestionId, "교육기획 답변입니다.", null)
        ));

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(request)
        );

        String applicationResponse = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.organizationRoleName").value("백엔드"))
                .andExpect(jsonPath("$.result.appliedPositions[*]").value(containsInAnyOrder("교육기획팀", "백엔드")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long applicationId = ((Number) JsonPath.read(applicationResponse, "$.result.id")).longValue();

        entityManager.flush();
        entityManager.clear();

        List<String> answerTexts = entityManager.createQuery("""
                        select answer.answerText
                        from ApplicationAnswer answer
                        where answer.application.id = :applicationId
                        """, String.class)
                .setParameter("applicationId", applicationId)
                .getResultList();

        assertThat(answerTexts).containsExactlyInAnyOrder("공통 답변입니다.", "백엔드 답변입니다.", "교육기획 답변입니다.");

        mockMvc.perform(get("/api/v1/applications/" + applicationId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.appliedPosition").value("백엔드"))
                .andExpect(jsonPath("$.result.appliedPositions[*]").value(containsInAnyOrder("교육기획팀", "백엔드")))
                .andExpect(jsonPath("$.result.documentAnswers[*].answerText")
                        .value(containsInAnyOrder("공통 답변입니다.", "백엔드 답변입니다.", "교육기획 답변입니다.")));
    }

    @Test
    @DisplayName("지원서 생성 실패 - 다중 역할 공고에서 필수 역할 그룹 미선택")
    void createApplicationWithMissingRequiredRoleGroupShouldReturnBadRequest() throws Exception {
        Long generalGroupId = createOrganizationRoleGroup("일반 파트");
        Long executiveGroupId = createOrganizationRoleGroup("운영진 팀");

        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long educationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "교육기획팀", accessToken);

        assignRolesToGroup(generalGroupId, List.of(backendRoleId));
        assignRolesToGroup(executiveGroupId, List.of(educationRoleId));

        Long recruitmentId = createRecruitmentWithRoles("필수 그룹 검증 공고", List.of(backendRoleId, educationRoleId));

        Map<String, Object> request = baseApplicationRequest(recruitmentId, "missing-group@example.com");
        request.put("positionIds", List.of(backendRoleId));

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관리자 지원서 목록 조회 - 다중 선택 역할 목록 응답 및 선택 역할 기준 필터")
    void getAdminApplicationsWithMultipleSelectedRolesShouldUseAllSelectedRoles() throws Exception {
        Long generalGroupId = createOrganizationRoleGroup("일반 파트");
        Long executiveGroupId = createOrganizationRoleGroup("운영진 팀");

        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(savedOrganizationId, "디자인", accessToken);
        Long educationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "교육기획팀", accessToken);
        Long managementRoleId = testHelper.createOrganizationRole(savedOrganizationId, "경영총괄팀", accessToken);

        assignRolesToGroup(generalGroupId, List.of(backendRoleId, designRoleId));
        assignRolesToGroup(executiveGroupId, List.of(educationRoleId, managementRoleId));

        Long recruitmentId = createRecruitmentWithRoles(
                "관리자 다중 파트 목록 공고",
                List.of(backendRoleId, designRoleId, educationRoleId, managementRoleId)
        );

        Long educationBackendApplicationId = createApplicationWithPositionIds(recruitmentId, "education-backend@example.com", List.of(educationRoleId, backendRoleId));
        createApplicationWithPositionIds(recruitmentId, "management-design@example.com", List.of(managementRoleId, designRoleId));

        entityManager.flush();
        entityManager.clear();

        List<String> selectedRoleNames = entityManager.createQuery("""
                        select role.name
                        from ApplicationOrganizationRole link
                        join link.organizationRole role
                        where link.application.email = :email
                        """, String.class)
                .setParameter("email", "education-backend@example.com")
                .getResultList();

        assertThat(selectedRoleNames).containsExactlyInAnyOrder("교육기획팀", "백엔드");

        mockMvc.perform(get("/api/v1/admin/applications/recruitment/" + recruitmentId)
                        .header("Authorization", accessToken)
                        .param("organizationRoleIds", String.valueOf(educationRoleId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data[0].id").value(educationBackendApplicationId))
                .andExpect(jsonPath("$.result.data[0].name").value("다중지원자"))
                .andExpect(jsonPath("$.result.data[0].organizationRoleName").value("백엔드"))
                .andExpect(jsonPath("$.result.data[0].appliedPositions[*]").value(containsInAnyOrder("교육기획팀", "백엔드")))
                .andExpect(jsonPath("$.result.data.length()").value(1));
    }

    @Test
    @DisplayName("관리자 지원서 목록 조회 - 다중 선택 역할 목록 기준 파트명 정렬")
    void getAdminApplicationsSortByPositionNameShouldUseAllSelectedRoles() throws Exception {
        Long generalGroupId = createOrganizationRoleGroup("일반 파트");
        Long executiveGroupId = createOrganizationRoleGroup("운영진 팀");

        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long educationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "교육기획팀", accessToken);
        Long managementRoleId = testHelper.createOrganizationRole(savedOrganizationId, "경영총괄팀", accessToken);

        assignRolesToGroup(generalGroupId, List.of(backendRoleId));
        assignRolesToGroup(executiveGroupId, List.of(educationRoleId, managementRoleId));

        Long recruitmentId = createRecruitmentWithRoles(
                "관리자 다중 파트 정렬 공고",
                List.of(backendRoleId, educationRoleId, managementRoleId)
        );

        createApplicationWithPositionIds(recruitmentId, "education-backend@example.com", List.of(educationRoleId, backendRoleId));
        Long managementBackendApplicationId = createApplicationWithPositionIds(recruitmentId, "management-backend@example.com", List.of(managementRoleId, backendRoleId));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/v1/admin/applications/recruitment/" + recruitmentId)
                        .header("Authorization", accessToken)
                        .param("sortBy", "POSITION_NAME")
                        .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data[0].id").value(managementBackendApplicationId))
                .andExpect(jsonPath("$.result.data[0].organizationRoleName").value("백엔드"))
                .andExpect(jsonPath("$.result.data[0].appliedPositions[*]").value(containsInAnyOrder("경영총괄팀", "백엔드")));
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
                .andExpect(jsonPath("$.result.acquainted").value(true));
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/v1/applications/" + appId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.acquaintanceCount").value(1))
                .andExpect(jsonPath("$.result.acquaintances[0].name").value("테스트유저"));

        mockMvc.perform(patch("/api/v1/applications/" + appId + "/acquaintance")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.acquainted").value(false));
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/v1/applications/" + appId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.acquaintanceCount").value(0));
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

    @Test
    @DisplayName("서류 평가 목록 조회 - 사용자 역할과 다중 선택 역할이 매칭되면 조회")
    void getApplicationsByRecruitmentForEvaluatorShouldIncludeApplicationsMatchingAnySelectedRole() throws Exception {
        Long generalGroupId = createOrganizationRoleGroup("일반 파트");
        Long executiveGroupId = createOrganizationRoleGroup("운영진 팀");

        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long educationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "교육기획팀", accessToken);
        Long managementRoleId = testHelper.createOrganizationRole(savedOrganizationId, "경영총괄팀", accessToken);

        assignRolesToGroup(generalGroupId, List.of(backendRoleId));
        assignRolesToGroup(executiveGroupId, List.of(educationRoleId, managementRoleId));

        var recruitmentRequest = new RecruitmentRequestDTO.Upsert(
                null, "역할 기반 평가 목록 공고", "설명",
                List.of(backendRoleId, educationRoleId, managementRoleId),
                List.of(),
                LocalDate.now().plusDays(5), true,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, savedOrganizationId,
                false, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(
                        new EvaluationCriteriaRequestDTO.Create("공통 평가", "", EvaluationType.DOCUMENT, null),
                        new EvaluationCriteriaRequestDTO.Create("백엔드 평가", "", EvaluationType.DOCUMENT, backendRoleId),
                        new EvaluationCriteriaRequestDTO.Create("경영총괄 평가", "", EvaluationType.DOCUMENT, managementRoleId)
                ),
                List.of(),
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

        Long applicationId = createApplicationWithPositionIds(
                recruitmentId,
                "management-backend-role-match@example.com",
                List.of(managementRoleId, backendRoleId)
        );

        User backendEvaluator = createEvaluatorUser("백엔드평가자", "backend-evaluator@example.com");
        assignRolesToUser(backendEvaluator.getId(), List.of(educationRoleId, backendRoleId));
        String evaluatorAccessToken = testAuthHelper.loginAndGetAccessToken("backend-evaluator@example.com", "password1!");

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/v1/applications/recruitment/" + recruitmentId)
                        .header("Authorization", evaluatorAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.length()").value(1))
                .andExpect(jsonPath("$.result.data[0].id").value(applicationId))
                .andExpect(jsonPath("$.result.data[0].appliedPositions[*]").value(containsInAnyOrder("경영총괄팀", "백엔드")))
                .andExpect(jsonPath("$.result.data[0].documentMaxScore").value(20));
    }

    private Long createOrganizationRoleGroup(String name) throws Exception {
        String payload = """
                {
                  "name": "%s",
                  "selectionMinCount": 1,
                  "selectionMaxCount": 1
                }
                """.formatted(name);

        String response = mockMvc.perform(post("/api/v1/organizations/" + savedOrganizationId + "/role-groups")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.result.id")).longValue();
    }

    private void assignRolesToGroup(Long groupId, List<Long> roleIds) throws Exception {
        Map<String, Object> payload = Map.of("roleIds", roleIds);

        mockMvc.perform(put("/api/v1/organizations/" + savedOrganizationId + "/role-groups/" + groupId + "/roles")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    private void assignRolesToUser(Long userId, List<Long> roleIds) throws Exception {
        Map<String, Object> payload = Map.of(
                "userId", userId,
                "roleIds", roleIds
        );

        mockMvc.perform(post("/api/v1/organizations/" + savedOrganizationId + "/assign-role")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    private User createEvaluatorUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .birthDate(LocalDate.of(1995, 1, 1))
                .role(Role.USER)
                .email(email)
                .phoneNumber("01099998888")
                .password(encoder.encode("password1!"))
                .build();
        return userRepository.save(user);
    }

    private Long createApplicationWithPositionIds(Long recruitmentId, String email, List<Long> positionIds) throws Exception {
        Map<String, Object> request = baseApplicationRequest(recruitmentId, email);
        request.put("positionIds", positionIds);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(request)
        );

        String response = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.result.id")).longValue();
    }

    private Long findQuestionId(Long recruitmentId, String title) {
        return recruitmentRepository.getById(recruitmentId).getQuestions().stream()
                .filter(question -> question.getTitle().equals(title))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    private Long createRecruitmentWithRoles(String title, List<Long> roleIds) throws Exception {
        var recruitmentRequest = new RecruitmentRequestDTO.Upsert(
                null, title, "설명",
                roleIds,
                List.of(),
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
        return ((Number) JsonPath.read(recruitmentResponse, "$.result.recruitmentId")).longValue();
    }

    private Map<String, Object> baseApplicationRequest(Long recruitmentId, String email) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("name", "다중지원자");
        request.put("email", email);
        request.put("phoneNumber", "01012341234");
        request.put("gender", Gender.MALE);
        request.put("university", "상명대학교");
        request.put("major", "컴퓨터공학과");
        request.put("academicStatus", AcademicStatus.ENROLLED);
        request.put("birthDate", LocalDate.of(2001, 1, 1));
        request.put("address", "서울시");
        request.put("recruitmentId", recruitmentId);
        request.put("answers", List.of());
        request.put("availableTimes", List.of(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0))));
        return request;
    }

}
