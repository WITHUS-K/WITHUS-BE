package KUSITMS.WITHUS.integration.domain.recruitment.recruitment.controller;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.dto.DocumentQuestionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.documentQuestion.enumerate.QuestionType;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.service.UserOrganizationService;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import KUSITMS.WITHUS.integration.util.TestAuthHelper;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    private final String testMail = "testMail@gmail.com";
    private Long savedOrganizationId;
    private Long savedRecruitmentId;
    private String accessToken;

    @BeforeEach
    void setup() throws Exception {
        savedOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("테스트 조직")).id();
        createTestUser();
        accessToken = testAuthHelper.loginAndGetAccessToken(testMail, "password1!");
        savedRecruitmentId = createRecruitment("기본 공고").id();
    }

    private void createTestUser() {
        String testPhone = "01000001111";
        var user = User.builder()
                .name("테스트유저")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.USER)
                .gender(Gender.FEMALE)
                .email(testMail)
                .phoneNumber(testPhone)
                .password(encoder.encode("password1!"))
                .build();
        userRepository.save(user);
    }

    private record RecruitmentResult(Long id, String slug) {}

    private RecruitmentResult createRecruitment(String title) throws Exception {
        var request = createUpsertRequest(title);
        var content = objectMapper.writeValueAsString(request);

        var response = performWithAuth(post("/api/v1/recruitments/publish"), content)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Number recruitmentId = JsonPath.read(response, "$.result.recruitmentId");
        String slug = null;
        if (response.contains("UrlSlug")) {
            slug = JsonPath.read(response, "$.result.UrlSlug");
        }

        return new RecruitmentResult(recruitmentId.longValue(), slug);
    }


    private RecruitmentRequestDTO.Upsert createUpsertRequest(String title) {
        return new RecruitmentRequestDTO.Upsert(
                null, title, "설명",
                List.of("백엔드"),
                List.of(new DocumentQuestionRequestDTO.Create("질문", "", QuestionType.TEXT, true, 500, true, null, null, null)),
                LocalDate.now().plusDays(5),
                true, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 10, savedOrganizationId,
                true, true, true, true, true, false,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(new EvaluationCriteriaRequestDTO.Create("기준1", "", EvaluationType.DOCUMENT)),
                List.of(new EvaluationCriteriaRequestDTO.Create("기준2", "", EvaluationType.INTERVIEW)),
                true,
                List.of(new AvailableTimeRangeRequestDTO(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(12, 0)))
        );
    }

    private ResultActions performWithAuth(MockHttpServletRequestBuilder requestBuilder, String content) throws Exception {
        return mockMvc.perform(requestBuilder
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
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
    @DisplayName("공고 삭제 성공")
    void deleteRecruitment() throws Exception {
        mockMvc.perform(delete("/api/v1/recruitments/" + savedRecruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("리크루팅 삭제에 성공하였습니다."));
    }

    @Test
    @DisplayName("공고 목록 조회")
    void getRecruitmentsSuccess() throws Exception {
        var request = createUpsertRequest("테스트 공고 제목");
        var content = objectMapper.writeValueAsString(request);

        performWithAuth(post("/api/v1/recruitments/publish"), content)
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/recruitments")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].title").value("테스트 공고 제목"))
                .andExpect(jsonPath("$.result[0].organizationName").value("테스트 조직"));
    }

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
        Long userId = userRepository.getByEmail(testMail).getId();

        userOrganizationService.addUserToOrganization(savedOrganizationId, List.of(userId));

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
}
