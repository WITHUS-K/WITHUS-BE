package KUSITMS.WITHUS.integration.util;

import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TestAuthHelper {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;

    public String loginAndGetAccessToken(String email, String password) throws Exception {
        UserRequestDTO.Login request = new UserRequestDTO.Login(email, password);
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getHeader("Authorization");
    }
}
