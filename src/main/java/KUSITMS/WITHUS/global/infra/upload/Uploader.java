package KUSITMS.WITHUS.global.infra.upload;

import org.springframework.web.multipart.MultipartFile;

public interface Uploader {
    String upload(MultipartFile file);
}
