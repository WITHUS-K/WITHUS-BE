package KUSITMS.WITHUS.global.infra.upload.controller;

import KUSITMS.WITHUS.global.infra.upload.dto.FileRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URL;

@RestController
@RequiredArgsConstructor
@Tag(name = "파일 Controller")
@RequestMapping("/api/v1/files")
public class FileController {

    @PostMapping("/download")
    @Operation(summary = "파일 다운로드 api", description = "브라우저에서 이미지 다운로드")
    public ResponseEntity<InputStreamResource> downloadImage(
            @RequestBody FileRequestDTO.Download request
            ) {
        try {
            InputStream in = new URL(request.imageUrl()).openStream();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + request.fileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
