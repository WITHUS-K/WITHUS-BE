package KUSITMS.WITHUS.integration.global.infra.upload.controller;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.infra.upload.dto.FileRequestDTO;
import KUSITMS.WITHUS.integration.config.MockInfraBeans;
import KUSITMS.WITHUS.integration.util.TestAuthHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MockInfraBeans.class)
@TestPropertySource(properties = "ncp.storage.endpoint=http://127.0.0.1")
class FileControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private UserRepository userRepository;
    @Autowired private TestAuthHelper testAuthHelper;

    @TempDir
    Path tempDir;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .name("파일관리자")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ADMIN)
                .email("file-admin@example.com")
                .phoneNumber("01044445555")
                .password(encoder.encode("password1!"))
                .build();
        userRepository.save(user);
        accessToken = testAuthHelper.loginAndGetAccessToken("file-admin@example.com", "password1!");
    }

    @Test
    @DisplayName("파일 URL 다운로드 성공")
    void downloadFileSuccess() throws Exception {
        HttpServer server = startFileServer("download-content");

        try {
            var request = new FileRequestDTO.Download("http://127.0.0.1:" + server.getAddress().getPort() + "/dummy-bucket/source.txt", "결과 파일.txt");

            mockMvc.perform(post("/api/v1/files/download")
                            .header("Authorization", accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=%EA%B2%B0%EA%B3%BC%20%ED%8C%8C%EC%9D%BC.txt"))
                    .andExpect(content().bytes("download-content".getBytes()));
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("로컬 파일 URL 다운로드 실패")
    void downloadFileFailWithFileScheme() throws Exception {
        Path source = tempDir.resolve("source.txt");
        Files.writeString(source, "local-file-content");
        var request = new FileRequestDTO.Download(source.toUri().toURL().toString(), "source.txt");

        mockMvc.perform(post("/api/v1/files/download")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                          "code": "FILE400",
                          "message": "잘못된 이미지 URL 형식입니다",
                          "success": false
                        }
                        """));
    }

    @Test
    @DisplayName("허용되지 않은 호스트의 파일 URL 다운로드 실패")
    void downloadFileFailWithDisallowedHost() throws Exception {
        var request = new FileRequestDTO.Download("https://invalid.localhost/not-found.png", "not-found.png");

        mockMvc.perform(post("/api/v1/files/download")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                          "code": "FILE400",
                          "message": "잘못된 이미지 URL 형식입니다",
                          "success": false
                        }
                        """));
    }

    private HttpServer startFileServer(String responseBody) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/dummy-bucket/source.txt", exchange -> {
            byte[] bytes = responseBody.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        return server;
    }
}
