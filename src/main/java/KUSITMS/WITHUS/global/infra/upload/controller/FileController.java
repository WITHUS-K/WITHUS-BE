package KUSITMS.WITHUS.global.infra.upload.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "파일 Controller")
@RequestMapping("/api/v1/files")
public class FileController {

    @GetMapping("/download")
    @Operation(summary = "파일 다운로드 api", description = "브라우저에서 이미지 다운로드")
    public ResponseEntity<InputStreamResource> downloadImage(
            @RequestParam String imageUrl,
            @RequestParam String fileName
    ) {
        try {
            InputStream in = new URL(imageUrl).openStream();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
