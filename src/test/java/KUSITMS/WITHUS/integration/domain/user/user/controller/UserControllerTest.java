package KUSITMS.WITHUS.integration.domain.user.user.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.global.util.redis.VerificationCache;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VerificationCache verificationCache;

    @Autowired
    private OrganizationService organizationService;

    Long savedOrganizationId;

    @BeforeEach
    void setup() {
        OrganizationRequestDTO.Create createRequest = new OrganizationRequestDTO.Create("테스트 조직");
        OrganizationResponseDTO.Create response = organizationService.create(createRequest);

        savedOrganizationId = response.id();

        verificationCache.markVerified("01012345678", Duration.ofMinutes(3));
    }

    @Test
    @DisplayName("관리자 회원가입 성공")
    void adminJoinSuccess() throws Exception {
        UserRequestDTO.AdminJoin request = new UserRequestDTO.AdminJoin(
                "관리자",
                "큐시즘",
                "admin@example.com",
                "AdminPass1!",
                "01012345678"
        );

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/users/join/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPasswordSuccess() throws Exception {
        UserRequestDTO.UserJoin joinReq = new UserRequestDTO.UserJoin(
                "김재관",
                LocalDate.of(2001, 4, 23),
                Gender.MALE, savedOrganizationId,
                "test@example.com",
                "Password1!",
                "01012345678"
        );

        String requestBody = objectMapper.writeValueAsString(joinReq);

        mockMvc.perform(post("/api/v1/users/join/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verificationCache.markVerified("test@example.com", Duration.ofMinutes(3));

        UserRequestDTO.ResetPassword resetReq =
                new UserRequestDTO.ResetPassword("test@example.com", "NewPassword1!");

        mockMvc.perform(post("/api/v1/users/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("사용자 회원가입 성공")
    void userJoinSuccess() throws Exception {
        // Given
        UserRequestDTO.UserJoin req = new UserRequestDTO.UserJoin(
                "김재관",
                LocalDate.of(2001, 4, 23),
                Gender.MALE,
                savedOrganizationId,
                "test2@example.com",
                "Password1!",
                "01012345678"
        );

        // When
        // Then
        mockMvc.perform(post("/api/v1/users/join/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.success").value(true));
    }

}