package KUSITMS.WITHUS.global.infra.upload.uploader;

import org.springframework.web.multipart.MultipartFile;

public interface Uploader {
    String upload(MultipartFile file, String pathPrefix);
}
