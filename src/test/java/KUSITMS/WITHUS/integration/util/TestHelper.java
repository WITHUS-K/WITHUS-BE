package KUSITMS.WITHUS.integration.util;

import KUSITMS.WITHUS.domain.application.application.dto.ApplicationRequestDTO;
import KUSITMS.WITHUS.domain.application.application.enumerate.AcademicStatus;
import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationScaleType;
import KUSITMS.WITHUS.domain.recruitment.availableTimeRange.dto.AvailableTimeRangeRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.recruitment.dto.RecruitmentRequestDTO;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TestHelper {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    public Long createRecruitment(String title, Long organizationId, String accessToken) throws Exception {
        List<AvailableTimeRangeRequestDTO> availableTimeRanges = List.of(
                new AvailableTimeRangeRequestDTO(
                        LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0)
                )
        );


        String recruitmentPayload = objectMapper.writeValueAsString(new RecruitmentRequestDTO.Upsert(
                null, title, "설명",
                List.of("백엔드"),
                List.of(),
                LocalDate.now().plusDays(5), true,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15),
                (short) 30, organizationId, true, true, true, true, true, false,
                EvaluationScaleType.SCORE, EvaluationScaleType.SCORE,
                List.of(), List.of(),
                true,
                availableTimeRanges
        ));

        MvcResult result = mockMvc.perform(post("/api/v1/recruitments/publish")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recruitmentPayload))
                .andExpect(status().isOk())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.recruitmentId")).longValue();
    }

    public Long createInterview(Long recruitmentId, String accessToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/interviews/recruitments/" + recruitmentId + "/interviews")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result")).longValue();
    }

    public Long createPosition(Long recruitmentId, String positionName, String accessToken) throws Exception {
        String payload = objectMapper.writeValueAsString(new PositionRequestDTO.Create(
                positionName, recruitmentId
        ));

        MvcResult result = mockMvc.perform(post("/api/v1/positions")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.id")).longValue();
    }

    public Long createApplication(String accessToken, Long recruitmentId, Long positionId, String name, String email) throws Exception {
        ApplicationRequestDTO.Create requestDto = new ApplicationRequestDTO.Create(
                name, email, "01012341234", Gender.MALE,
                "대학교", "전공", AcademicStatus.ENROLLED,
                LocalDate.of(2000, 1, 1), "서울시",
                recruitmentId, positionId,
                List.of(), List.of(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)))
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request", "", "application/json", objectMapper.writeValueAsBytes(requestDto)
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/applications")
                        .file(jsonPart)
                        .header("Authorization", accessToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.result.id")).longValue();
    }
}

