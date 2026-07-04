package KUSITMS.WITHUS.integration.domain.organization.organizationRoleGroup.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.organization.organizationRoleGroup.dto.OrganizationRoleGroupRequestDTO;
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
class OrganizationRoleGroupControllerTest {

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
        organizationId = organizationService.create(new OrganizationRequestDTO.Create("테스트 조직")).id();
        createUser("관리자", "admin@example.com", "01000001111", Role.ADMIN);
        accessToken = testAuthHelper.loginAndGetAccessToken("admin@example.com", "password1!");
    }

    @Test
    @DisplayName("조직 역할 그룹 생성 및 역할 배정 조회 성공")
    void createAssignAndGetOrganizationRoleGroupSuccess() throws Exception {
        Long backendRoleId = testHelper.createOrganizationRole(organizationId, "백엔드", accessToken);
        Long designRoleId = testHelper.createOrganizationRole(organizationId, "디자인", accessToken);
        Long groupId = createRoleGroup("일반 파트", 1, 1);
        var assignRequest = new OrganizationRoleGroupRequestDTO.AssignRoles(List.of(backendRoleId, designRoleId));

        mockMvc.perform(put("/api/v1/organizations/" + organizationId + "/role-groups/" + groupId + "/roles")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roles", hasSize(2)))
                .andExpect(jsonPath("$.result.roles[*].roleName", containsInAnyOrder("백엔드", "디자인")));

        mockMvc.perform(get("/api/v1/organizations/" + organizationId + "/role-groups")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(1)))
                .andExpect(jsonPath("$.result[0].name").value("일반 파트"))
                .andExpect(jsonPath("$.result[0].selectionMinCount").value(1))
                .andExpect(jsonPath("$.result[0].selectionMaxCount").value(1))
                .andExpect(jsonPath("$.result[0].roles[*].roleName", containsInAnyOrder("백엔드", "디자인")));
    }

    @Test
    @DisplayName("다른 조직 역할을 역할 그룹에 배정하면 실패")
    void assignOtherOrganizationRoleToGroupFail() throws Exception {
        Long otherOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("다른 조직")).id();
        Long otherRoleId = testHelper.createOrganizationRole(otherOrganizationId, "교육기획팀", accessToken);
        Long groupId = createRoleGroup("운영진 팀", 1, 1);
        var assignRequest = new OrganizationRoleGroupRequestDTO.AssignRoles(List.of(otherRoleId));

        mockMvc.perform(put("/api/v1/organizations/" + organizationId + "/role-groups/" + groupId + "/roles")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ORGANIZATION_ROLE403"));
    }

    private Long createRoleGroup(String name, int selectionMinCount, int selectionMaxCount) throws Exception {
        var request = new OrganizationRoleGroupRequestDTO.Create(name, selectionMinCount, selectionMaxCount);

        MvcResult result = mockMvc.perform(post("/api/v1/organizations/" + organizationId + "/role-groups")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.id")).longValue();
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
}
