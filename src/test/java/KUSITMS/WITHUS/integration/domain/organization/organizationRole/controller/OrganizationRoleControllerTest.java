package KUSITMS.WITHUS.integration.domain.organization.organizationRole.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.organization.organizationRole.dto.OrganizationRoleRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
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

import java.time.LocalDate;
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
class OrganizationRoleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private OrganizationService organizationService;
    @Autowired private UserRepository userRepository;
    @Autowired private TestAuthHelper testAuthHelper;
    @Autowired private TestHelper testHelper;

    private Long organizationId;
    private Long targetUserId;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        organizationId = organizationService.create(new OrganizationRequestDTO.Create("테스트 조직")).id();
        createUser("관리자", "admin@example.com", "01000001111", Role.ADMIN);
        targetUserId = createUser("운영진", "staff@example.com", "01000002222", Role.USER);
        accessToken = testAuthHelper.loginAndGetAccessToken("admin@example.com", "password1!");
    }

    @Test
    @DisplayName("조직 역할 생성 및 조회 성공")
    void createAndGetOrganizationRolesSuccess() throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(organizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(organizationId, "디자인", accessToken);

        mockMvc.perform(get("/api/v1/organizations/" + organizationId + "/roles")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalRoleCount").value(2))
                .andExpect(jsonPath("$.result.roles[*].id", containsInAnyOrder(
                        backendRoleId.intValue(),
                        designRoleId.intValue()
                )))
                .andExpect(jsonPath("$.result.roles[*].roleName", containsInAnyOrder("백엔드", "디자인")))
                .andExpect(jsonPath("$.result.roles[*].assignedUserCount", containsInAnyOrder(0, 0)));
    }

    @Test
    @DisplayName("조직 내 중복 역할명 생성 실패")
    void createOrganizationRoleDuplicateNameFail() throws Exception {
        testHelper.createOrganizationRole(organizationId, "백엔드", accessToken);

        var request = new OrganizationRoleRequestDTO.Create("백엔드", "#00FF00");

        mockMvc.perform(post("/api/v1/organizations/" + organizationId + "/roles")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORGANIZATION_ROLE400"));
    }

    @Test
    @DisplayName("운영진에게 조직 내 여러 역할 부여 성공")
    void assignMultipleRolesToUserSuccess() throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(organizationId, "백엔드", accessToken);
        Long educationRoleId = testHelper.createOrganizationRole(organizationId, "교육기획팀", accessToken);
        var request = new OrganizationRoleRequestDTO.Assign(targetUserId, List.of(backendRoleId, educationRoleId));

        mockMvc.perform(post("/api/v1/organizations/" + organizationId + "/assign-role")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[*].userName", containsInAnyOrder("운영진", "운영진")))
                .andExpect(jsonPath("$.result[*].roleName", containsInAnyOrder("백엔드", "교육기획팀")));

        mockMvc.perform(get("/api/v1/organizations/" + organizationId + "/roles")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roles[?(@.roleName == '백엔드')].assignedUserCount").value(1))
                .andExpect(jsonPath("$.result.roles[?(@.roleName == '교육기획팀')].assignedUserCount").value(1));
    }

    @Test
    @DisplayName("운영진 역할 재부여 시 요청에 없는 기존 역할 제거")
    void reassignUserRolesRemovesMissingRole() throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(organizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(organizationId, "디자인", accessToken);

        assignRoles(targetUserId, List.of(backendRoleId, designRoleId));

        var request = new OrganizationRoleRequestDTO.Assign(targetUserId, List.of(designRoleId));

        mockMvc.perform(post("/api/v1/organizations/" + organizationId + "/assign-role")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].userName").value("운영진"))
                .andExpect(jsonPath("$.result[0].roleName").value("디자인"));

        mockMvc.perform(get("/api/v1/organizations/" + organizationId + "/roles")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roles[?(@.roleName == '백엔드')].assignedUserCount").value(0))
                .andExpect(jsonPath("$.result.roles[?(@.roleName == '디자인')].assignedUserCount").value(1));
    }

    @Test
    @DisplayName("다른 조직 역할을 운영진에게 부여하면 실패")
    void assignRoleFromOtherOrganizationFail() throws Exception {
        Long otherOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("다른 조직")).id();
        Long otherRoleId = testHelper.createOrganizationRole(otherOrganizationId, "대외협력팀", accessToken);
        var request = new OrganizationRoleRequestDTO.Assign(targetUserId, List.of(otherRoleId));

        mockMvc.perform(post("/api/v1/organizations/" + organizationId + "/assign-role")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ORGANIZATION_ROLE403"));
    }

    private Long createUser(String name, String email, String phoneNumber, Role role) {
        User user = User.builder()
                .name(name)
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(role)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(encoder.encode("password1!"))
                .build();
        return userRepository.save(user).getId();
    }

    private void assignRoles(Long userId, List<Long> roleIds) throws Exception {
        var request = new OrganizationRoleRequestDTO.Assign(userId, roleIds);

        mockMvc.perform(post("/api/v1/organizations/" + organizationId + "/assign-role")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
