package KUSITMS.WITHUS.integration.global.auth.controller;

import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private UserRepository userRepository;

    private final String email = "auth-user@example.com";
    private final String password = "password1!";

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("인증유저")
                .birthDate(LocalDate.of(1995, 1, 1))
                .role(Role.ADMIN)
                .email(email)
                .phoneNumber("01012345678")
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("로그인 성공 시 access token과 refresh token을 헤더로 반환")
    void loginSuccessReturnsTokens() throws Exception {
        UserRequestDTO.Login request = new UserRequestDTO.Login(email, password);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("Refresh-Token"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.name").value("인증유저"))
                .andExpect(jsonPath("$.result.role").value("ADMIN"));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFailWithWrongPassword() throws Exception {
        UserRequestDTO.Login request = new UserRequestDTO.Login(email, "wrongPassword1!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("USER401"));
    }

    @Test
    @DisplayName("Refresh Token으로 Access Token 재발급 성공")
    void reissueAccessTokenSuccess() throws Exception {
        MvcResult loginResult = login();
        String refreshToken = loginResult.getResponse().getHeader("Refresh-Token");

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .header("Refresh-Token", refreshToken))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", org.hamcrest.Matchers.startsWith("Bearer ")));
    }

    @Test
    @DisplayName("로그아웃 후 기존 Refresh Token 재발급 실패")
    void reissueFailAfterLogout() throws Exception {
        MvcResult loginResult = login();
        String accessToken = loginResult.getResponse().getHeader("Authorization");
        String refreshToken = loginResult.getResponse().getHeader("Refresh-Token");

        assertThat(accessToken).startsWith("Bearer ");
        assertThat(refreshToken).isNotBlank();

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .header("Refresh-Token", refreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("TOKEN401"));
    }

    private MvcResult login() throws Exception {
        UserRequestDTO.Login request = new UserRequestDTO.Login(email, password);

        return mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
    }
}
