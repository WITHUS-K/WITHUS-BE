package KUSITMS.WITHUS.integration.domain.evaluation.evaluation.controller;

import KUSITMS.WITHUS.domain.evaluation.evaluation.dto.EvaluationRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.dto.EvaluationCriteriaRequestDTO;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class EvaluationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private TestAuthHelper testAuthHelper;
    @Autowired private TestHelper testHelper;
    @Autowired private UserRepository userRepository;
    @Autowired private OrganizationService organizationService;
    @Autowired private RecruitmentRepository recruitmentRepository;
    @Autowired private EntityManager entityManager;

    private final String testMail = "evaluation-test@gmail.com";
    private Long savedOrganizationId;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        savedOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("평가 테스트 조직")).id();
        createTestUser();
        accessToken = testAuthHelper.loginAndGetAccessToken(testMail, "password1!");
    }

    private void createTestUser() {
        User user = User.builder()
                .name("평가자")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email(testMail)
                .phoneNumber("01000002222")
                .password(encoder.encode("password1!"))
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("지원서 평가 등록 성공")
    void evaluateApplicationSuccess() throws Exception {
        EvaluationTarget target = createEvaluationTarget(List.of("성실성"));

        EvaluationRequestDTO.Create request = new EvaluationRequestDTO.Create(
                target.applicationId(),
                target.criteriaIdsByContent().get("성실성"),
                8
        );

        mockMvc.perform(post("/api/v1/evaluations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.score").value(8))
                .andExpect(jsonPath("$.result.criteria.content").value("성실성"))
                .andExpect(jsonPath("$.result.user.name").value("평가자"));
    }

    @Test
    @DisplayName("같은 평가자가 동일 지원서와 동일 기준에 중복 평가하면 실패")
    void evaluateApplicationDuplicateFail() throws Exception {
        EvaluationTarget target = createEvaluationTarget(List.of("성실성"));
        EvaluationRequestDTO.Create request = new EvaluationRequestDTO.Create(
                target.applicationId(),
                target.criteriaIdsByContent().get("성실성"),
                8
        );

        mockMvc.perform(post("/api/v1/evaluations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/evaluations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("EVALUATION400"));
    }

    @Test
    @DisplayName("지원서 평가 벌크 등록 성공")
    void bulkEvaluateApplicationSuccess() throws Exception {
        EvaluationTarget target = createEvaluationTarget(List.of("성실성", "전문성"));

        EvaluationRequestDTO.BulkCreate request = new EvaluationRequestDTO.BulkCreate(
                target.applicationId(),
                List.of(
                        new EvaluationRequestDTO.BulkCreate.EvaluationItem(
                                target.criteriaIdsByContent().get("성실성"),
                                7
                        ),
                        new EvaluationRequestDTO.BulkCreate.EvaluationItem(
                                target.criteriaIdsByContent().get("전문성"),
                                9
                        )
                )
        );

        mockMvc.perform(post("/api/v1/evaluations/bulk")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[*].score", containsInAnyOrder(7, 9)))
                .andExpect(jsonPath("$.result[*].criteria.content", containsInAnyOrder("성실성", "전문성")))
                .andExpect(jsonPath("$.result[*].user.name", containsInAnyOrder("평가자", "평가자")));
    }

    private EvaluationTarget createEvaluationTarget(List<String> criteriaContents) throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        List<EvaluationCriteriaRequestDTO.Create> criteriaRequests = criteriaContents.stream()
                .map(content -> new EvaluationCriteriaRequestDTO.Create(
                        content,
                        content + " 설명",
                        EvaluationType.DOCUMENT,
                        backendRoleId
                ))
                .toList();
        RecruitmentRequestDTO.Upsert recruitmentRequest = new RecruitmentRequestDTO.Upsert(
                null, "평가 테스트 공고", "설명",
                List.of(backendRoleId),
                List.of(),
                LocalDate.now().plusDays(5), true,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, savedOrganizationId,
                false, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                criteriaRequests,
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
        entityManager.flush();
        entityManager.clear();

        Map<String, Long> criteriaIdsByContent = new LinkedHashMap<>();
        recruitmentRepository.getById(recruitmentId).getEvaluationCriteriaList()
                .forEach(criteria -> criteriaIdsByContent.put(criteria.getContent(), criteria.getId()));
        Long applicationId = testHelper.createApplication(
                accessToken,
                recruitmentId,
                backendRoleId,
                "평가지원자",
                "evaluation-applicant@example.com"
        );

        return new EvaluationTarget(applicationId, criteriaIdsByContent);
    }

    private record EvaluationTarget(Long applicationId, Map<String, Long> criteriaIdsByContent) {
    }
}
