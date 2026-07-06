package KUSITMS.WITHUS.integration.domain.template.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.template.dto.TemplateRequestDTO;
import KUSITMS.WITHUS.domain.template.enumerate.Medium;
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
class TemplateControllerTest {

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
        organizationId = organizationService.create(new OrganizationRequestDTO.Create("템플릿 테스트 조직")).id();
        User user = createUser("템플릿관리자", "template-admin@example.com", "01033334444");
        associateUserWithOrganization(user, organizationRepository.getById(organizationId));
        accessToken = testAuthHelper.loginAndGetAccessToken("template-admin@example.com", "password1!");
    }

    @Test
    @DisplayName("메일 템플릿 생성, 조회, 목록, 수정, 삭제 성공")
    void createGetListUpdateAndDeleteTemplateSuccess() throws Exception {
        Long templateId = createTemplate(accessToken, organizationId, "면접 안내", Medium.MAIL);

        mockMvc.perform(get("/api/v1/templates/" + templateId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("면접 안내"))
                .andExpect(jsonPath("$.result.subject").value("[WITHUS] 면접 안내"))
                .andExpect(jsonPath("$.result.medium").value("MAIL"));

        mockMvc.perform(get("/api/v1/templates")
                        .header("Authorization", accessToken)
                        .param("medium", "MAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[*].name", hasItem("면접 안내")))
                .andExpect(jsonPath("$.result[*].organizationName", hasItem("템플릿 테스트 조직")));

        var updateRequest = new TemplateRequestDTO.Update(
                "최종 합격 안내",
                "[WITHUS] 최종 합격 안내",
                "최종 합격을 축하합니다.",
                Medium.MAIL
        );

        mockMvc.perform(put("/api/v1/templates/" + templateId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("최종 합격 안내"))
                .andExpect(jsonPath("$.result.subject").value("[WITHUS] 최종 합격 안내"))
                .andExpect(jsonPath("$.result.body").value("최종 합격을 축하합니다."));

        mockMvc.perform(delete("/api/v1/templates/" + templateId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("템플릿이 삭제되었습니다."));
    }

    @Test
    @DisplayName("소속되지 않은 조직의 템플릿은 수정할 수 없다")
    void updateTemplateForbiddenWhenUserIsNotInOrganization() throws Exception {
        Long templateId = createTemplate(accessToken, organizationId, "불가 템플릿", Medium.SMS);

        Long otherOrganizationId = organizationService.create(new OrganizationRequestDTO.Create("다른 템플릿 조직")).id();
        User otherUser = createUser("다른관리자", "other-template-admin@example.com", "01033335555");
        associateUserWithOrganization(otherUser, organizationRepository.getById(otherOrganizationId));
        String otherAccessToken = testAuthHelper.loginAndGetAccessToken("other-template-admin@example.com", "password1!");

        var updateRequest = new TemplateRequestDTO.Update(
                "수정 시도",
                null,
                "권한 없는 수정",
                Medium.SMS
        );

        mockMvc.perform(put("/api/v1/templates/" + templateId)
                        .header("Authorization", otherAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("TEMPLATE403"));
    }

    private Long createTemplate(String token, Long orgId, String name, Medium medium) throws Exception {
        var request = new TemplateRequestDTO.Create(
                name,
                orgId,
                medium == Medium.MAIL ? "[WITHUS] " + name : null,
                name + " 본문",
                medium
        );

        MvcResult result = mockMvc.perform(post("/api/v1/templates")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value(name))
                .andExpect(jsonPath("$.result.medium").value(medium.name()))
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.id")).longValue();
    }

    private User createUser(String name, String email, String phoneNumber) {
        User user = User.builder()
                .name(name)
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email(email)
                .phoneNumber(phoneNumber)
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
