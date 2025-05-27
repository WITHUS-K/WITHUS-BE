package KUSITMS.WITHUS.domain.interview.interview.dto;

import KUSITMS.WITHUS.domain.interview.interview.entity.Interview;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "면접 관련 응답 DTO")
public class InterviewResponseDTO {

    @Schema(description = "면접 구성 응답 DTO")
    public record Config(
            @Schema(description = "면접실 이름 배열") List<String> roomNames,
            @Schema(description = "면접관 수") Integer interviewerCount,
            @Schema(description = "지원자 수") Integer applicantCount,
            @Schema(description = "안내자 수") Integer assistantCount
    ) {
        public static Config from(Interview interview, Integer interviewerCount, Integer applicantCount, Integer assistantCount) {
            return new Config(
                    interview.getRoomNames(),
                    interviewerCount,
                    applicantCount,
                    assistantCount
            );
        }
    }
}
