package KUSITMS.WITHUS.integration.domain.organization.organization.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class OrganizationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private OrganizationService organizationService;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserOrganizationRepository userOrganizationRepository;
    @Autowired private TestAuthHelper testAuthHelper;

    private Long organizationId;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        organizationId = organizationService.create(new OrganizationRequestDTO.Create("테스트 조직")).id();
        User user = createUser();
        Organization organization = organizationRepository.getById(organizationId);
        associateUserWithOrganization(user, organization);
        accessToken = testAuthHelper.loginAndGetAccessToken("organization-admin@example.com", "password1!");
    }

    @Test
    @DisplayName("조직 생성, 단건 조회, 수정, 검색 성공")
    void createGetUpdateAndSearchOrganizationSuccess() throws Exception {
        var createRequest = new OrganizationRequestDTO.Create("큐시즘");
        MvcResult createResult = mockMvc.perform(post("/api/v1/organizations")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("큐시즘"))
                .andReturn();
        Long createdOrganizationId = ((Number) JsonPath.read(
                createResult.getResponse().getContentAsString(),
                "$.result.id"
        )).longValue();

        mockMvc.perform(get("/api/v1/organizations/" + createdOrganizationId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(createdOrganizationId))
                .andExpect(jsonPath("$.result.name").value("큐시즘"));

        var updateRequest = new OrganizationRequestDTO.Update("큐시즘 리뉴얼");

        mockMvc.perform(put("/api/v1/organizations/" + createdOrganizationId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(createdOrganizationId))
                .andExpect(jsonPath("$.result.name").value("큐시즘 리뉴얼"));

        mockMvc.perform(get("/api/v1/organizations/search")
                        .param("keyword", "큐시즘"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[*].name", hasItem("큐시즘 리뉴얼")));
    }

    @Test
    @DisplayName("조직 소속 사용자는 초대 코드를 생성하고 코드로 조직을 조회할 수 있다")
    void generateInviteCodeAndExchangeSuccess() throws Exception {
        MvcResult inviteResult = mockMvc.perform(get("/api/v1/organizations/" + organizationId + "/code")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(organizationId))
                .andExpect(jsonPath("$.result.name").value("테스트 조직"))
                .andReturn();
        String inviteCode = JsonPath.read(inviteResult.getResponse().getContentAsString(), "$.result.inviteCode");

        assertThat(inviteCode).isNotBlank();

        mockMvc.perform(get("/api/v1/organizations/inviteCode/exchange")
                        .param("inviteCode", inviteCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(organizationId))
                .andExpect(jsonPath("$.result.name").value("테스트 조직"));

        mockMvc.perform(get("/api/v1/organizations/" + organizationId + "/code")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.inviteCode").value(inviteCode));
    }

    @Test
    @DisplayName("조직에 속하지 않은 사용자는 초대 코드를 조회할 수 없다")
    void generateInviteCodeForbiddenWhenUserIsNotInOrganization() throws Exception {
        Long otherOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("다른 조직")).id();

        mockMvc.perform(get("/api/v1/organizations/" + otherOrganizationId + "/code")
                        .header("Authorization", accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("COMMON403"));
    }

    private User createUser() {
        User user = User.builder()
                .name("조직관리자")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email("organization-admin@example.com")
                .phoneNumber("01011112222")
                .password(encoder.encode("password1!"))
                .build();
        return userRepository.save(user);
    }

    private void associateUserWithOrganization(User user, Organization organization) {
        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .build();
        user.addUserOrganization(userOrganization);
        organization.addUserOrganization(userOrganization);
        userOrganizationRepository.save(userOrganization);
    }
}
