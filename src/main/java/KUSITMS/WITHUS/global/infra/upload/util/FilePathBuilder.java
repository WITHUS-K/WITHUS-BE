package KUSITMS.WITHUS.global.infra.upload.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FilePathBuilder {

    /**
     * 저장할 파일 경로를 생성
     * @param domainTopLevel  루트 디렉토리명
     * @param organizationId  조직 ID
     * @param recruitmentId  공고 ID
     * @param entityId  엔티티 ID (applicationId 등)
     * @param type  파일 유형 (profile, answer 등)
     * @param originalFilename  원본 파일명
     * @return  구성된 파일 경로
     */
    public String buildUploadPath(String domainTopLevel, Long organizationId, Long recruitmentId, Long entityId, String type, String originalFilename) {
        String safeFileName = UUID.randomUUID() + "_" + originalFilename;
        return String.format("%s/%d/%d/%d/%s/%s", domainTopLevel, organizationId, recruitmentId, entityId, type, safeFileName);
    }
}

