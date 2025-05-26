package KUSITMS.WITHUS.integration.domain.user.user.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.global.util.redis.VerificationCache;
import KUSITMS.WITHUS.integration.common.IntegrationTestSupport;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import KUSITMS.WITHUS.integration.util.TestAuthHelper;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class UserControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VerificationCache verificationCache;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestAuthHelper testAuthHelper;

    @Autowired private BCryptPasswordEncoder encoder;

    Long savedOrganizationId;
    private final String testMail = "testMail@gmail.com";
    private final String testPhone = "01000001111";

    @BeforeEach
    void setup() {
        OrganizationRequestDTO.Create createRequest = new OrganizationRequestDTO.Create("테스트 조직");
        OrganizationResponseDTO.Create response = organizationService.create(createRequest);

        savedOrganizationId = response.id();

        User testUser = User.builder()
                .name("테스트유저")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.USER)
                .gender(Gender.FEMALE)
                .email(testMail)
                .phoneNumber(testPhone)
                .password(encoder.encode("password1!"))
                .build();

        userRepository.save(testUser);

        verificationCache.markVerified(testPhone, Duration.ofMinutes(3));
        verificationCache.markVerified(testMail, Duration.ofMinutes(3));
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
        verificationCache.markVerified("01012345678", Duration.ofMinutes(3));

        mockMvc.perform(post("/api/v1/users/join/admin")
                        .contentType(APPLICATION_JSON)
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
        verificationCache.markVerified("01012345678", Duration.ofMinutes(3));
        verificationCache.markVerified("test@example.com", Duration.ofMinutes(3));

        mockMvc.perform(post("/api/v1/users/join/user")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        UserRequestDTO.ResetPassword resetReq =
                new UserRequestDTO.ResetPassword("test@example.com", "NewPassword1!");

        mockMvc.perform(post("/api/v1/users/reset-password")
                        .contentType(APPLICATION_JSON)
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
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("이메일 중복 확인 - 중복")
    void checkEmailDuplicate_duplicated() throws Exception {
        mockMvc.perform(get("/api/v1/users/email/check")
                        .param("email", testMail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isDuplicated").value(true));
    }

    @Test
    @DisplayName("이메일 단건 조회")
    void getUserByEmailSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/email")
                        .param("email", testMail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.result.name").value("테스트유저"))
                .andExpect(jsonPath("$.result.email").value(testMail));
    }

    @Test
    @DisplayName("마이페이지 조회")
    void getMyPageSuccess() throws Exception {
        String accessToken = testAuthHelper.loginAndGetAccessToken(testMail, "password1!");

        mockMvc.perform(get("/api/v1/users/my-page")
                    .header("Authorization", accessToken)
                    .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("테스트유저"))
                .andExpect(jsonPath("$.result.phoneNumber").value(testPhone))
                .andExpect(jsonPath("$.result.email").value(testMail));
    }

    @Test
    @DisplayName("회원 정보 수정")
    void updateUserSuccess() throws Exception {
        String accessToken = testAuthHelper.loginAndGetAccessToken(testMail, "password1!");

        var updateReq = new UserRequestDTO.Update("홍길동", "01099999999", null, null, null);
        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateReq)
        );

        mockMvc.perform(multipart("/api/v1/users")
                        .file(jsonPart)
                        .with(r -> { r.setMethod("PATCH"); return r; })
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("홍길동"))
                .andExpect(jsonPath("$.result.phoneNumber").value("01099999999"));
    }
}