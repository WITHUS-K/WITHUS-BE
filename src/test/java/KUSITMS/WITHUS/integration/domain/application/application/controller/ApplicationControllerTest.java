package KUSITMS.WITHUS.integration.domain.application.application.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.domain.application.enumerate.ApplicationStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    @Autowired private UserRepository userRepository;
    @Autowired private OrganizationService organizationService;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private RecruitmentRepository recruitmentRepository;
    @Autowired private PositionRepository positionRepository;

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
                .gender(Gender.FEMALE)
                .email(testMail)
                .phoneNumber(testPhone)
                .password(encoder.encode("password1!"))
                .build();
        userRepository.save(user);
    }

    private Recruitment createRecruitment(String title) {
        Organization organization = organizationRepository.getById(savedOrganizationId);

        Recruitment recruitment = Recruitment.builder()
                .title(title)
                .content("테스트 공고 내용")
                .organization(organization)
                .urlSlug(UUID.randomUUID().toString())
                .documentDeadline(LocalDate.now().plusDays(5))
                .documentResultDate(LocalDate.now().plusDays(10))
                .finalResultDate(LocalDate.now().plusDays(15))
                .interviewDuration((short) 30)
                .documentScaleType(EvaluationScaleType.SCORE)
                .interviewScaleType(EvaluationScaleType.SCORE)
                .needGender(true)
                .needAddress(true)
                .needSchool(true)
                .needBirthDate(true)
                .needMajor(true)
                .needAcademicStatus(true)
                .isDocumentResultRequired(true)
                .isInterviewRequired(true)
                .isTemporary(false)
                .build();

        return recruitmentRepository.save(recruitment);
    }

    private Position createPosition(String name, Recruitment recruitment) {
        Position position = Position.builder()
                .name(name)
                .color("#000000")
                .recruitment(recruitment)
                .build();

        return positionRepository.save(position);
    }


    @Test
    @DisplayName("지원서 생성 성공")
    void createApplicationSuccess() throws Exception {
        Recruitment recruitment = createRecruitment("지원서 테스트용 공고");
        Position position = createPosition("백엔드", recruitment);

        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "김재관", "test@example.com", "01012341234", Gender.MALE,
                "상명대학교", "컴퓨터공학과", AcademicStatus.ENROLLED,
                LocalDate.of(2001, 1, 1), "서울시 도봉구 56로 501",
                recruitment.getId(),
                position.getId(),
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
                .andExpect(jsonPath("$.result.positionName").value(position.getName()))
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
    @DisplayName("지원서 단건 조회 성공")
    void getApplicationByIdSuccess() throws Exception {
        Recruitment recruitment = createRecruitment("조회용 공고");
        Position position = createPosition("프론트엔드", recruitment);

        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "홍길동", "user@example.com", "01099998888", Gender.MALE,
                "서울대학교", "컴퓨터공학과", AcademicStatus.GRADUATED,
                LocalDate.of(1995, 5, 5), "서울시 강남구",
                recruitment.getId(), position.getId(),
                List.of(), List.of(LocalDateTime.of(2025, 5, 1, 14, 0))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );

        String response = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Number applicationId = JsonPath.read(response, "$.result.id");

        mockMvc.perform(get("/api/v1/applications/" + applicationId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("홍길동"))
                .andExpect(jsonPath("$.result.email").value("user@example.com"));
    }

    @Test
    @DisplayName("지원서 단건 조회 실패 - 존재하지 않는 ID")
    void getApplication_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/applications/999999")
                        .header("Authorization", accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("지원서 삭제 성공")
    void deleteApplicationSuccess() throws Exception {
        Recruitment recruitment = createRecruitment("삭제용 공고");
        Position position = createPosition("디자인", recruitment);

        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "삭제대상", "delete@example.com", "01033334444", Gender.FEMALE,
                "연세대학교", "시각디자인", AcademicStatus.LEAVE_OF_ABSENCE,
                LocalDate.of(1999, 12, 12), "서울시 마포구",
                recruitment.getId(), position.getId(),
                List.of(), List.of(LocalDateTime.of(2025, 5, 3, 11, 0))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );

        String response = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Number applicationId = JsonPath.read(response, "$.result.id");

        mockMvc.perform(delete("/api/v1/applications/" + applicationId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("지원서 삭제에 성공하였습니다."));
    }

    @Test
    @DisplayName("지원서 지인 여부 토글 성공")
    void toggleAcquaintanceSuccess() throws Exception {
        Recruitment recruitment = createRecruitment("지인 테스트 공고");
        Position position = createPosition("기획", recruitment);

        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "박토글", "toggle@example.com", "01088887777", Gender.MALE,
                "고려대학교", "경영학과", AcademicStatus.DEFERRED,
                LocalDate.of(1997, 3, 3), "서울시 송파구",
                recruitment.getId(), position.getId(),
                List.of(), List.of(LocalDateTime.of(2025, 4, 30, 16, 0))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );

        String response = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Number applicationId = JsonPath.read(response, "$.result.id");

        mockMvc.perform(patch("/api/v1/applications/" + applicationId + "/acquaintance")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.acquainted").isBoolean());
    }

    @Test
    @DisplayName("서류 평가 담당자의 공고별 평가 대상 지원서 목록 조회 성공")
    void getApplicationsByRecruitmentForEvaluator() throws Exception {
        // Given
        Recruitment recruitment = createRecruitment("서류 평가 공고");
        Position position = createPosition("백엔드", recruitment);

        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                "김김김", "eval@example.com", "01011112222", Gender.MALE,
                "상명대학교", "AI전공", AcademicStatus.ENROLLED,
                LocalDate.of(2000, 2, 22), "서울시 서대문구",
                recruitment.getId(), position.getId(),
                List.of(), List.of(LocalDateTime.of(2025, 5, 1, 15, 0))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );

        String response = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Number idRaw = JsonPath.read(response, "$.result.id");
        Long applicationId = idRaw.longValue();

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
        mockMvc.perform(get("/api/v1/applications/recruitment/" + recruitment.getId())
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data[0].name").value("김김김"))
                .andExpect(jsonPath("$.result.data[0].positionName").value(position.getName()))
                .andExpect(jsonPath("$.result.data[0].status").value(ApplicationStatus.PENDING.name()));
    }

}