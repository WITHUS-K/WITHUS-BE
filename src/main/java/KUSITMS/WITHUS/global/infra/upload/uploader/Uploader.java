package KUSITMS.WITHUS.global.infra.upload.uploader;

import KUSITMS.WITHUS.global.infra.upload.dto.FileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface Uploader {
    FileResponseDTO.Upload upload(MultipartFile file, String pathPrefix);
    void delete(String key);
}
