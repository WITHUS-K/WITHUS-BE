package KUSITMS.WITHUS.integration.domain.recruitment.position.controller;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import KUSITMS.WITHUS.integration.util.TestAuthHelper;
import KUSITMS.WITHUS.integration.util.TestHelper;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class PositionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private OrganizationService organizationService;
    @Autowired private UserRepository userRepository;
    @Autowired private TestAuthHelper testAuthHelper;
    @Autowired private TestHelper testHelper;

    private Long organizationId;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        organizationId = organizationService.create(new OrganizationRequestDTO.Create("파트 테스트 조직")).id();
        createUser();
        accessToken = testAuthHelper.loginAndGetAccessToken("position-admin@example.com", "password1!");
    }

    @Test
    @DisplayName("파트 생성 및 삭제 성공")
    void createAndDeletePositionSuccess() throws Exception {
        Long recruitmentId = createRecruitment(List.of());
        var request = new PositionRequestDTO.Create("레거시 파트", recruitmentId);

        MvcResult result = mockMvc.perform(post("/api/v1/positions")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("레거시 파트"))
                .andReturn();
        Long positionId = ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.id")).longValue();

        mockMvc.perform(delete("/api/v1/positions/" + positionId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("공고별 파트 조회는 공고에 연결된 조직 역할을 반환한다")
    void findAllByRecruitmentIdReturnsOrganizationRoles() throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(organizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(organizationId, "디자인", accessToken);
        Long recruitmentId = createRecruitment(List.of(backendRoleId, designRoleId));

        mockMvc.perform(get("/api/v1/positions/recruitment/" + recruitmentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[*].id", containsInAnyOrder(
                        backendRoleId.intValue(),
                        designRoleId.intValue()
                )))
                .andExpect(jsonPath("$.result[*].name", containsInAnyOrder("백엔드", "디자인")));
    }

    private Long createRecruitment(List<Long> roleIds) throws Exception {
        RecruitmentRequestDTO.Upsert request = new RecruitmentRequestDTO.Upsert(
                null,
                "파트 테스트 공고",
                "설명",
                roleIds,
                List.of(),
                LocalDate.now().plusDays(5),
                true,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                (short) 30,
                organizationId,
                false,
                true,
                true,
                true,
                true,
                false,
                true,
                EvaluationScaleType.SCORE,
                EvaluationScaleType.SCORE,
                List.of(),
                List.of(),
                true,
                List.of(new AvailableTimeRangeRequestDTO(
                        LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0)
                ))
        );

        MvcResult result = mockMvc.perform(post("/api/v1/recruitments/publish")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.recruitmentId")).longValue();
    }

    private void createUser() {
        User user = User.builder()
                .name("파트관리자")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email("position-admin@example.com")
                .phoneNumber("01022223333")
                .password(encoder.encode("password1!"))
                .build();
        userRepository.save(user);
    }
}
