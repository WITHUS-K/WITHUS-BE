package KUSITMS.WITHUS.global.infra.upload.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FilePathBuilder {

    /**
     * 저장할 파일 경로를 생성
     * @param parts 경로 구성 요소들 (폴더명, ID 등)
     * @return  구성된 파일 경로
     */
    public String buildUploadPath(String... parts) {
        List<String> safeParts = Arrays.stream(parts)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        String uuid = UUID.randomUUID().toString();
        String originalFilename = safeParts.remove(safeParts.size() - 1); // 마지막은 파일명
        String safeFileName = uuid + "_" + originalFilename;

        safeParts.add(safeFileName);
        return String.join("/", safeParts);
    }
}

