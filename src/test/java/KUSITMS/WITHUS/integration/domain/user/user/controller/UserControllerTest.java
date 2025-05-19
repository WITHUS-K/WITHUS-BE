package KUSITMS.WITHUS.integration.domain.user.user.controller;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.organization.service.OrganizationService;
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

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
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
    @DisplayName("사용자 회원가입 성공")
    void userJoinSuccess() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "김서진",
                "birthDate": "2003-02-22",
                "gender": "MALE",
                "organizationId": 1,
                "email": "seojin@example.com",
                "password": "Password1!",
                "phoneNumber": "01012345678"
            }
        """;

        // When
        // Then
        mockMvc.perform(post("/api/v1/users/join/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공하였습니다."))
                .andExpect(jsonPath("$.success").value(true));
    }

}