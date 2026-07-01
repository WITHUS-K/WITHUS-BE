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
import java.util.List;

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
        Long backendRoleId = testHelper.createOrganizationRole(savedOrganizationId, "백엔드", accessToken);
        RecruitmentRequestDTO.Upsert recruitmentRequest = new RecruitmentRequestDTO.Upsert(
                null, "평가 테스트 공고", "설명",
                List.of(backendRoleId),
                List.of(),
                LocalDate.now().plusDays(5), true,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, savedOrganizationId,
                false, true, true, true, true, false, true,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(new EvaluationCriteriaRequestDTO.Create("성실성", "꾸준히 참여할 수 있는지", EvaluationType.DOCUMENT, backendRoleId)),
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

        Long criteriaId = recruitmentRepository.getById(recruitmentId).getEvaluationCriteriaList().stream()
                .filter(criteria -> criteria.getContent().equals("성실성"))
                .findFirst()
                .orElseThrow()
                .getId();
        Long applicationId = testHelper.createApplication(
                accessToken,
                recruitmentId,
                backendRoleId,
                "평가지원자",
                "evaluation-applicant@example.com"
        );

        EvaluationRequestDTO.Create request = new EvaluationRequestDTO.Create(applicationId, criteriaId, 8);

        mockMvc.perform(post("/api/v1/evaluations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.score").value(8))
                .andExpect(jsonPath("$.result.criteria.content").value("성실성"))
                .andExpect(jsonPath("$.result.user.name").value("평가자"));
    }
}
