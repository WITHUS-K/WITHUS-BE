package KUSITMS.WITHUS.integration.domain.interview.interview.controller;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AdminStageFilter;
import KUSITMS.WITHUS.domain.application.application.enumerate.SimpleApplicationStatus;
import KUSITMS.WITHUS.domain.interview.interview.service.InterviewSchedulerService;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.user.service.UserService;
import KUSITMS.WITHUS.global.util.redis.VerificationCache;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import KUSITMS.WITHUS.integration.util.TestAuthHelper;
import KUSITMS.WITHUS.integration.util.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.Duration;
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
class InterviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TestHelper testHelper;
    @Autowired private TestAuthHelper testAuthHelper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private PositionRepository positionRepository;
    @Autowired private VerificationCache verificationCache;

    private String accessToken;
    private Long savedOrganizationId;

    private final String testMail = "admin@gmail.com";
    private final String testPassword = "password1!";
    private final String testPhone = "01012345678";

    @BeforeEach
    void setUp() throws Exception {
        verificationCache.markVerified(testPhone, Duration.ofMinutes(3));
        createTestUser();
        accessToken = testAuthHelper.loginAndGetAccessToken(testMail, testPassword);
    }

    private void createTestUser() {
        userService.adminJoinProcess(new UserRequestDTO.AdminJoin(
                "관리자", "큐시즘", testMail, testPassword, testPhone));
        savedOrganizationId = userRepository.getByEmail(testMail)
                .getUserOrganizations().get(0).getOrganization().getId();
    }

    private Long createAndScheduleInterview(Long recruitmentId) throws Exception {
        Long interviewId = testHelper.createInterview(recruitmentId, accessToken);
        InterviewSchedulerService.InterviewConfig config = new InterviewSchedulerService.InterviewConfig(
                2, 2, 2, 2, List.of("Room A", "Room B"));
        mockMvc.perform(post("/api/v1/interviews/recruitments/" + recruitmentId + "/interviews/" + interviewId + "/schedule")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk());
        return interviewId;
    }

    private void updateApplicationStatus(List<Long> appIds) throws Exception {
        ApplicationRequestDTO.UpdateStatus updateStatus = new ApplicationRequestDTO.UpdateStatus(
                appIds, AdminStageFilter.DOCUMENT, SimpleApplicationStatus.PASS);
        mockMvc.perform(patch("/api/v1/admin/applications/status")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatus)))
                .andExpect(status().isOk());
    }

    private Long prepareInterviewScenario() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("면접 테스트용 공고", savedOrganizationId, accessToken);

        Long positionId1 = testHelper.createPosition(recruitmentId, "백엔드", accessToken);
        Long positionId2 = testHelper.createPosition(recruitmentId, "프론트엔드", accessToken);

        Long appId1 = testHelper.createApplication(accessToken, recruitmentId, positionId1, "지원자1", "applicant1@example.com");
        Long appId2 = testHelper.createApplication(accessToken, recruitmentId, positionId2, "지원자2", "applicant2@example.com");

        updateApplicationStatus(List.of(appId1, appId2));
        return createAndScheduleInterview(recruitmentId);
    }

    @Test
    @DisplayName("면접 생성 성공")
    void createInterviewSuccess() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("면접 테스트용 공고", savedOrganizationId, accessToken);
        mockMvc.perform(post("/api/v1/interviews/recruitments/" + recruitmentId + "/interviews")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."));
    }

    @Test
    @DisplayName("면접 스케줄 배정 성공")
    void assignScheduleSuccess() throws Exception {
        Long recruitmentId = testHelper.createRecruitment("면접 테스트용 공고", savedOrganizationId, accessToken);
        Long positionId = testHelper.createPosition(recruitmentId, "파트", accessToken);
        Long appId1 = testHelper.createApplication(accessToken, recruitmentId, positionId, "지원자1", "app1@example.com");
        Long appId2 = testHelper.createApplication(accessToken, recruitmentId, positionId, "지원자2", "app2@example.com");

        updateApplicationStatus(List.of(appId1, appId2));

        InterviewSchedulerService.InterviewConfig config = new InterviewSchedulerService.InterviewConfig(
                2, 2, 2, 2, List.of("Room A", "Room B"));

        Long interviewId = testHelper.createInterview(recruitmentId, accessToken);

        mockMvc.perform(post("/api/v1/interviews/recruitments/" + recruitmentId + "/interviews/" + interviewId + "/schedule")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(config)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("면접 타임테이블 생성에 성공하였습니다."));
    }

    @Test
    @DisplayName("전체 면접 스케줄 조회 성공")
    void getScheduleSuccess() throws Exception {
        Long interviewId = prepareInterviewScenario();

        mockMvc.perform(get("/api/v1/interviews/" + interviewId + "/schedule")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].timeSlots.length()").value(2))
                .andExpect(jsonPath("$.result[0].startTime").value("10:00"));
    }

    @Test
    @DisplayName("내 면접 시간 조회 성공")
    void getMyInterviewTimeSuccess() throws Exception {
        Long interviewId = prepareInterviewScenario();

        mockMvc.perform(get("/api/v1/interviews/" + interviewId + "/my-time-slots")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].interviewId").value(interviewId))
                .andExpect(jsonPath("$.result[0].startTime").value("10:00"));
    }

    @Test
    @DisplayName("내 조직 면접 정보 요약 조회 성공")
    void getMyOrgInterviewsSuccess() throws Exception {
        Long interviewId = prepareInterviewScenario();

        mockMvc.perform(get("/api/v1/interviews/my-organization-interviews")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].interviewId").value(interviewId.intValue()))
                .andExpect(jsonPath("$.result[0].recruitmentTitle").value("면접 테스트용 공고"))
                .andExpect(jsonPath("$.result[0].availableTimeRanges[0].startTime").value("10:00"))
                .andExpect(jsonPath("$.result[0].interviewDuration").value(30));
    }

    @Test
    @DisplayName("면접 구성 정보 조회 성공")
    void getInterviewConfigSuccess() throws Exception {
        Long interviewId = prepareInterviewScenario();

        mockMvc.perform(get("/api/v1/interviews/" + interviewId + "/config")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roomNames.length()").value(2))
                .andExpect(jsonPath("$.result.interviewerCount").value(2))
                .andExpect(jsonPath("$.result.applicantCount").value(2))
                .andExpect(jsonPath("$.result.assistantCount").value(2));
    }
}

