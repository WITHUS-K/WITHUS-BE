package KUSITMS.WITHUS.util;

import KUSITMS.WITHUS.global.infra.upload.uploader.Uploader;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class FakeUploader implements Uploader {

    @Override
    public String upload(MultipartFile file, String pathPrefix) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fakeUrl = String.format("https://fake-storage.com/%s/%s", pathPrefix != null ? pathPrefix : "test", fileName);
        System.out.println("📤 [FAKE 업로드] 파일: " + file.getOriginalFilename() + " → URL: " + fakeUrl);
        return fakeUrl;
    }

    @Override
    public void delete(String key) {
        System.out.println("🗑️ [FAKE 삭제] 키: " + key);
    }
}

