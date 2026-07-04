package KUSITMS.WITHUS.integration.domain.application.comment.controller;

import KUSITMS.WITHUS.domain.application.comment.dto.CommentRequestDTO;
import KUSITMS.WITHUS.domain.application.comment.enumerate.CommentType;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class CommentControllerTest {

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
        organizationId = organizationService.create(new OrganizationRequestDTO.Create("코멘트 테스트 조직")).id();
        createUser("코멘트작성자", "commenter@example.com", "01000003333");
        accessToken = testAuthHelper.loginAndGetAccessToken("commenter@example.com", "password1!");
    }

    @Test
    @DisplayName("지원서 코멘트 생성, 수정, 삭제 성공")
    void createUpdateAndDeleteCommentSuccess() throws Exception {
        Long applicationId = createApplication();
        Long commentId = createComment(applicationId, accessToken, "서류 검토 필요");

        var updateRequest = new CommentRequestDTO.Update("서류 검토 완료");

        mockMvc.perform(put("/api/v1/applications/" + applicationId + "/comments/" + commentId)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").value("서류 검토 완료"))
                .andExpect(jsonPath("$.result.type").value("DOCUMENT"))
                .andExpect(jsonPath("$.result.userName").value("코멘트작성자"));

        mockMvc.perform(delete("/api/v1/applications/" + applicationId + "/comments/" + commentId)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("다른 사용자가 작성한 코멘트는 수정할 수 없다")
    void updateCommentForbiddenWhenNotOwner() throws Exception {
        Long applicationId = createApplication();
        Long commentId = createComment(applicationId, accessToken, "작성자만 수정 가능");
        createUser("다른작성자", "other-commenter@example.com", "01000004444");
        String otherAccessToken = testAuthHelper.loginAndGetAccessToken("other-commenter@example.com", "password1!");

        var updateRequest = new CommentRequestDTO.Update("타인이 수정 시도");

        mockMvc.perform(put("/api/v1/applications/" + applicationId + "/comments/" + commentId)
                        .header("Authorization", otherAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("COMMON403"));
    }

    private Long createComment(Long applicationId, String token, String content) throws Exception {
        var request = new CommentRequestDTO.Create(content, CommentType.DOCUMENT);

        MvcResult result = mockMvc.perform(post("/api/v1/applications/" + applicationId + "/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").value(content))
                .andExpect(jsonPath("$.result.type").value("DOCUMENT"))
                .andExpect(jsonPath("$.result.user.name").value("코멘트작성자"))
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.id")).longValue();
    }

    private Long createApplication() throws Exception {
        Long roleId = testHelper.createOrganizationRole(organizationId, "백엔드", accessToken);
        Long recruitmentId = createRecruitment(roleId);
        return testHelper.createApplication(
                accessToken,
                recruitmentId,
                roleId,
                "코멘트지원자",
                "comment-applicant@example.com"
        );
    }

    private Long createRecruitment(Long roleId) throws Exception {
        RecruitmentRequestDTO.Upsert request = new RecruitmentRequestDTO.Upsert(
                null,
                "코멘트 테스트 공고",
                "설명",
                List.of(roleId),
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

    private void createUser(String name, String email, String phoneNumber) {
        User user = User.builder()
                .name(name)
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(encoder.encode("password1!"))
                .build();
        userRepository.save(user);
    }
}
