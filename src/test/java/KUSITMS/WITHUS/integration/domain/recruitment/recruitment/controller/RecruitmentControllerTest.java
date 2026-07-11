package KUSITMS.WITHUS.integration.domain.recruitment.recruitment.controller;

import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.service.UserOrganizationService;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import KUSITMS.WITHUS.integration.util.TestAuthHelper;
import KUSITMS.WITHUS.integration.util.TestHelper;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class RecruitmentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private OrganizationService organizationService;
    @Autowired private UserOrganizationService userOrganizationService;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private TestAuthHelper testAuthHelper;
    @Autowired private TestHelper testHelper;

    private final String testMail = "testMail@gmail.com";
    private Long savedOrganizationId;
    private Long savedRecruitmentId;
    private String accessToken;

    @BeforeEach
    void setup() throws Exception {
        savedOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("테스트 조직")).id();
        Long userId = createTestUser();
        addUserToOrganization(userId, savedOrganizationId);
        accessToken = testAuthHelper.loginAndGetAccessToken(testMail, "password1!");
        savedRecruitmentId = testHelper.createRecruitment("기본 공고", savedOrganizationId, accessToken);
    }

    private Long createTestUser() {
        String testPhone = "01000001111";
        var user = User.builder()
                .name("테스트유저")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.USER)
                .email(testMail)
                .phoneNumber(testPhone)
                .password(encoder.encode("password1!"))
                .build();
        return userRepository.save(user).getId();
    }

    private void addUserToOrganization(Long userId, Long organizationId) {
        userOrganizationService.addUserToOrganization(organizationId, List.of(userId));
    }

    private ResultActions performWithAuth(MockHttpServletRequestBuilder requestBuilder, String content) throws Exception {
        return mockMvc.perform(requestBuilder
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    private RecruitmentRequestDTO.Upsert createUpsertRequest(String title) {
        return new RecruitmentRequestDTO.Upsert(
                null, title, "설명",
                List.of(), // organizationRoleIds - 테스트에서는 빈 리스트
                List.of(new DocumentQuestionRequestDTO.Create("질문", "", QuestionType.TEXT, true, 500, true, null, null, null, 1)),
                LocalDate.now().plusDays(5),
                true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 10, savedOrganizationId,
                true, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(new EvaluationCriteriaRequestDTO.Create("기준1", "", EvaluationType.DOCUMENT, null)),
                List.of(new EvaluationCriteriaRequestDTO.Create("기준2", "", EvaluationType.INTERVIEW, null)),
                true,
                List.of(new AvailableTimeRangeRequestDTO(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0)))
        );
    }

    @Test
    @DisplayName("공고 임시 저장 성공")
    void saveDraftSuccess() throws Exception {
        var request = createUpsertRequest("임시 저장 공고");
        var content = objectMapper.writeValueAsString(request);

        performWithAuth(post("/api/v1/recruitments/draft"), content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("공고 최종 저장 성공")
    void createRecruitmentSuccess() throws Exception {
        var request = createUpsertRequest("2025-1 큐시즘 모집");
        var content = objectMapper.writeValueAsString(request);

        performWithAuth(post("/api/v1/recruitments/publish"), content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("공고 단건 상세 조회 성공")
    void getRecruitmentById() throws Exception {
        mockMvc.perform(get("/api/v1/recruitments/" + savedRecruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value("기본 공고"))
                .andExpect(jsonPath("$.result.content").value("설명"))
                .andExpect(jsonPath("$.result.organizationName").value("테스트 조직"))
                .andExpect(jsonPath("$.result.recruitmentId").value(savedRecruitmentId));
    }

    @Test
    @DisplayName("공고 상세 조회 성공 - 공통 질문과 역할별 질문 포함")
    void getRecruitmentByIdIncludesCommonAndRoleQuestions() throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(savedOrganizationId, "디자인", accessToken);
        var request = new RecruitmentRequestDTO.Upsert(
                null, "질문 포함 공고", "설명",
                List.of(backendRoleId, designRoleId),
                List.of(
                        new DocumentQuestionRequestDTO.Create("공통 질문", "", QuestionType.TEXT, true, 500, true, null, null, null, 1),
                        new DocumentQuestionRequestDTO.Create("백엔드 질문", "", QuestionType.TEXT, true, 500, true, null, null, backendRoleId, 2),
                        new DocumentQuestionRequestDTO.Create("디자인 질문", "", QuestionType.TEXT, true, 500, true, null, null, designRoleId, 3)
                ),
                LocalDate.now().plusDays(5),
                true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 10, savedOrganizationId,
                true, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(),
                true,
                List.of(new AvailableTimeRangeRequestDTO(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0)))
        );

        String createResponse = performWithAuth(post("/api/v1/recruitments/publish"), objectMapper.writeValueAsString(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long recruitmentId = ((Number) JsonPath.read(createResponse, "$.result.recruitmentId")).longValue();

        mockMvc.perform(get("/api/v1/recruitments/" + recruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.applicationQuestions[0].title").value("공통 질문"))
                .andExpect(jsonPath("$.result.applicationQuestions[0].organizationRoleName").value("공통"))
                .andExpect(jsonPath("$.result.applicationQuestions[1].title").value("백엔드 질문"))
                .andExpect(jsonPath("$.result.applicationQuestions[1].organizationRoleName").value("백엔드"))
                .andExpect(jsonPath("$.result.applicationQuestions[2].title").value("디자인 질문"))
                .andExpect(jsonPath("$.result.applicationQuestions[2].organizationRoleName").value("디자인"));
    }

    @Test
    @DisplayName("공고 상세 조회 성공 - 공고 역할이 포함된 조직 역할 그룹 반환")
    void getRecruitmentByIdIncludesRoleGroupsForRecruitmentPositions() throws Exception {
        Long generalGroupId = createOrganizationRoleGroup("일반 파트", 1, 1);
        Long executiveGroupId = createOrganizationRoleGroup("운영진 팀", 1, 1);
        Long unusedGroupId = createOrganizationRoleGroup("미사용 그룹", 1, 1);

        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(savedOrganizationId, "디자인", accessToken);
        Long educationRoleId = testHelper.createOrganizationRole(savedOrganizationId, "교육기획팀", accessToken);
        Long unusedRoleId = testHelper.createOrganizationRole(savedOrganizationId, "미사용 역할", accessToken);

        assignRolesToGroup(generalGroupId, List.of(backendRoleId, designRoleId));
        assignRolesToGroup(executiveGroupId, List.of(educationRoleId));
        assignRolesToGroup(unusedGroupId, List.of(unusedRoleId));

        var request = new RecruitmentRequestDTO.Upsert(
                null, "다중 파트 공고", "설명",
                List.of(backendRoleId, educationRoleId),
                List.of(),
                LocalDate.now().plusDays(5),
                true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 10, savedOrganizationId,
                true, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(),
                true,
                List.of(new AvailableTimeRangeRequestDTO(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0)))
        );

        String createResponse = performWithAuth(post("/api/v1/recruitments/publish"), objectMapper.writeValueAsString(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long recruitmentId = ((Number) JsonPath.read(createResponse, "$.result.recruitmentId")).longValue();

        mockMvc.perform(get("/api/v1/recruitments/" + recruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roleGroups.length()").value(2))
                .andExpect(jsonPath("$.result.roleGroups[0].name").value("일반 파트"))
                .andExpect(jsonPath("$.result.roleGroups[0].selectionMinCount").value(1))
                .andExpect(jsonPath("$.result.roleGroups[0].selectionMaxCount").value(1))
                .andExpect(jsonPath("$.result.roleGroups[0].roles.length()").value(1))
                .andExpect(jsonPath("$.result.roleGroups[0].roles[0].roleName").value("백엔드"))
                .andExpect(jsonPath("$.result.roleGroups[1].name").value("운영진 팀"))
                .andExpect(jsonPath("$.result.roleGroups[1].roles.length()").value(1))
                .andExpect(jsonPath("$.result.roleGroups[1].roles[0].roleName").value("교육기획팀"));
    }

    @Test
    @DisplayName("공고 목록과 홈 요약은 다중 선택된 모든 역할의 지원자 수를 카운트")
    void recruitmentSummariesCountApplicationsByAllSelectedRoles() throws Exception {
        Long generalGroupId = createOrganizationRoleGroup("일반 파트", 1, 1);
        Long executiveGroupId = createOrganizationRoleGroup("운영진 팀", 1, 1);

        Long planningRoleId = testHelper.createOrganizationRole(savedOrganizationId, "기획", accessToken);
        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        Long managementRoleId = testHelper.createOrganizationRole(savedOrganizationId, "경영총괄팀", accessToken);

        assignRolesToGroup(generalGroupId, List.of(planningRoleId, backendRoleId));
        assignRolesToGroup(executiveGroupId, List.of(managementRoleId));

        Long recruitmentId = createRecruitmentWithRoles(
                "다중 선택 카운트 공고",
                List.of(managementRoleId, planningRoleId, backendRoleId)
        );
        createApplicationWithPositionIds(
                recruitmentId,
                "management-planning@example.com",
                List.of(managementRoleId, planningRoleId)
        );

        mockMvc.perform(get("/api/v1/recruitments")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[?(@.recruitmentId == %d)].positionSummaries[?(@.name == '경영총괄팀')].applicantCount".formatted(recruitmentId)).value(contains(1)))
                .andExpect(jsonPath("$.result[?(@.recruitmentId == %d)].positionSummaries[?(@.name == '기획')].applicantCount".formatted(recruitmentId)).value(contains(1)))
                .andExpect(jsonPath("$.result[?(@.recruitmentId == %d)].positionSummaries[?(@.name == '백엔드')].applicantCount".formatted(recruitmentId)).value(contains(0)));

        String adminToken = createAdminAndLogin();

        mockMvc.perform(get("/api/v1/admin/recruitments/current/summary")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[?(@.recruitmentId == %d)].positionCounts[?(@.positionName == '경영총괄팀')].count".formatted(recruitmentId)).value(contains(1)))
                .andExpect(jsonPath("$.result[?(@.recruitmentId == %d)].positionCounts[?(@.positionName == '기획')].count".formatted(recruitmentId)).value(contains(1)))
                .andExpect(jsonPath("$.result[?(@.recruitmentId == %d)].positionCounts[?(@.positionName == '백엔드')].count".formatted(recruitmentId)).value(contains(0)));
    }

    @Test
    @DisplayName("공고 삭제 성공")
    void deleteRecruitment() throws Exception {
        mockMvc.perform(delete("/api/v1/recruitments/" + savedRecruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("리크루팅 삭제에 성공하였습니다."));
    }

//    @Test
//    @DisplayName("공고 목록 조회")
//    void getRecruitmentsSuccess() throws Exception {
//        testHelper.createRecruitment("테스트 공고 제목", savedOrganizationId, accessToken);
//
//        mockMvc.perform(get("/api/v1/recruitments")
//                        .header("Authorization", accessToken)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.result").isArray())
//                .andExpect(jsonPath("$.result[0].title").value("테스트 공고 제목"))
//                .andExpect(jsonPath("$.result[0].organizationName").value("테스트 조직"));
//    }

    @Test
    @DisplayName("공고 슬러그 조회 성공")
    void getBySlug() throws Exception {
        var detailResponse = mockMvc.perform(get("/api/v1/recruitments/" + savedRecruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String slug = JsonPath.read(detailResponse, "$.result.UrlSlug");

        mockMvc.perform(get("/api/v1/recruitments/slug/" + slug)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.UrlSlug").value(slug))
                .andExpect(jsonPath("$.result.title").value("기본 공고"))
                .andExpect(jsonPath("$.result.content").value("설명"))
                .andExpect(jsonPath("$.result.organizationName").value("테스트 조직"))
                .andExpect(jsonPath("$.result.recruitmentId").value(savedRecruitmentId));
    }

    @Test
    @DisplayName("내 조직의 공고 목록 조회 성공")
    void getAllMyOrganizationRecruitments() throws Exception {
        mockMvc.perform(get("/api/v1/recruitments/my-organizations")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].title").value("기본 공고"))
                .andExpect(jsonPath("$.result[0].recruitmentId").value(savedRecruitmentId))
                .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    @DisplayName("내 서류 평가 현황 조회 성공")
    void getMyDocumentEvaluations() throws Exception {
        mockMvc.perform(get("/api/v1/recruitments/" + savedRecruitmentId + "/my/evaluations/documents")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }

    private Long createOrganizationRoleGroup(String name, int selectionMinCount, int selectionMaxCount) throws Exception {
        var request = new OrganizationRoleGroupRequestDTO.Create(name, selectionMinCount, selectionMaxCount);

        MvcResult result = mockMvc.perform(post("/api/v1/organizations/" + savedOrganizationId + "/role-groups")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.result.id")).longValue();
    }

    private void assignRolesToGroup(Long groupId, List<Long> roleIds) throws Exception {
        var request = new OrganizationRoleGroupRequestDTO.AssignRoles(roleIds);

        mockMvc.perform(put("/api/v1/organizations/" + savedOrganizationId + "/role-groups/" + groupId + "/roles")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private Long createRecruitmentWithRoles(String title, List<Long> roleIds) throws Exception {
        var request = new RecruitmentRequestDTO.Upsert(
                null, title, "설명",
                roleIds,
                List.of(),
                LocalDate.now().plusDays(5),
                true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 10, savedOrganizationId,
                false, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(),
                true,
                List.of(new AvailableTimeRangeRequestDTO(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0)))
        );

        String response = performWithAuth(post("/api/v1/recruitments/publish"), objectMapper.writeValueAsString(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.result.recruitmentId")).longValue();
    }

    private Long createApplicationWithPositionIds(Long recruitmentId, String email, List<Long> positionIds) throws Exception {
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
        request.put("positionIds", positionIds);
        request.put("answers", List.of());
        request.put("availableTimes", List.of(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(10, 0))));

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(request)
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus(), result.getResponse().getContentAsString());

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.id")).longValue();
    }

    private String createAdminAndLogin() throws Exception {
        String email = "admin-summary@example.com";
        User admin = User.builder()
                .name("관리자")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email(email)
                .phoneNumber("01000002222")
                .password(encoder.encode("password1!"))
                .build();
        Long adminId = userRepository.save(admin).getId();
        addUserToOrganization(adminId, savedOrganizationId);
        return testAuthHelper.loginAndGetAccessToken(email, "password1!");
    }
}
