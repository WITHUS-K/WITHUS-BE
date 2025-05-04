package KUSITMS.WITHUS.global.infra.upload.service;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.upload.uploader.Uploader;
import KUSITMS.WITHUS.global.infra.upload.util.FilePathBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Uploader uploader;
    private final FilePathBuilder pathBuilder;

    @Value("${ncp.storage.bucket-name}")
    private String bucketName;

    public String uploadUserProfileImage(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) return null;

        String path = pathBuilder.buildUploadPath("users", String.valueOf(userId), "profile", file.getOriginalFilename());
        return uploader.upload(file, path);
    }

    public String uploadProfileImage(MultipartFile file, Long orgId, Long recruitmentId, Long appId) {
        if (file == null || file.isEmpty()) return null;

        String path = pathBuilder.buildUploadPath("applications", String.valueOf(orgId), String.valueOf(recruitmentId), String.valueOf(appId), "profile", file.getOriginalFilename());
        return uploader.upload(file, path);
    }

    public Map<String, String> uploadAnswerFiles(List<MultipartFile> files, Long orgId, Long recruitmentId, Long appId) {
        Map<String, String> result = new HashMap<>();
        if (files == null) return result;

        for (MultipartFile file : files) {
            String path = pathBuilder.buildUploadPath("applications", String.valueOf(orgId), String.valueOf(recruitmentId), String.valueOf(appId), "answer", file.getOriginalFilename());
            String url = uploader.upload(file, path);
            result.put(file.getOriginalFilename(), url);
        }

        return result;
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            URI uri = URI.create(imageUrl);
            String fullPath = uri.getPath();
            String prefix = "/" + bucketName + "/";

            String key = fullPath.substring(prefix.length());
            uploader.delete(key);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_URL);
        }
    }
}
