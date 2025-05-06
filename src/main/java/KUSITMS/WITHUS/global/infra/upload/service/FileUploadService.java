package KUSITMS.WITHUS.global.infra.upload.service;

import KUSITMS.WITHUS.global.infra.upload.uploader.Uploader;
import KUSITMS.WITHUS.global.infra.upload.util.FilePathBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Uploader uploader;
    private final FilePathBuilder pathBuilder;

    public String uploadProfileImage(MultipartFile file, Long orgId, Long recruitmentId, Long appId) {
        if (file == null || file.isEmpty()) return null;

        String path = pathBuilder.buildUploadPath("applications", orgId, recruitmentId, appId, "profile", file.getOriginalFilename());
        return uploader.upload(file, path);
    }

    public Map<String, String> uploadAnswerFiles(List<MultipartFile> files, Long orgId, Long recruitmentId, Long appId) {
        Map<String, String> result = new HashMap<>();
        if (files == null) return result;

        for (MultipartFile file : files) {
            String path = pathBuilder.buildUploadPath("applications", orgId, recruitmentId, appId, "answer", file.getOriginalFilename());
            String url = uploader.upload(file, path);
            result.put(file.getOriginalFilename(), url);
        }

        return result;
    }
}
