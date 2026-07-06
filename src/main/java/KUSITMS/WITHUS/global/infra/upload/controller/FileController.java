package KUSITMS.WITHUS.global.infra.upload.controller;

import KUSITMS.WITHUS.global.infra.upload.dto.FileRequestDTO;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "파일 Controller")
@RequestMapping("/api/v1/files")
public class FileController {

    private static final Set<String> ALLOWED_DOWNLOAD_SCHEMES = Set.of("http", "https");
    private static final int MAX_REDIRECT_COUNT = 5;
    private static final int CONNECT_TIMEOUT_MILLIS = 5_000;
    private static final int READ_TIMEOUT_MILLIS = 5_000;

    @Value("${ncp.storage.endpoint}")
    private String storageEndpoint;

    @Value("${ncp.storage.bucket-name}")
    private String bucketName;

    @PostMapping("/download")
    @Operation(summary = "파일 다운로드 api", description = "브라우저에서 이미지 다운로드")
    public ResponseEntity<InputStreamResource> downloadImage(
            @RequestBody FileRequestDTO.Download request
            ) {
        URL imageUrl = parseAllowedDownloadUrl(request.imageUrl());

        try {
            URLConnection connection = openAllowedConnection(imageUrl);
            InputStream in = connection.getInputStream();

            String encodedFileName = URLEncoder.encode(request.fileName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + encodedFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private URL parseAllowedDownloadUrl(String rawUrl) {
        try {
            URL url = new URL(rawUrl);
            if (!ALLOWED_DOWNLOAD_SCHEMES.contains(url.getProtocol().toLowerCase(Locale.ROOT))) {
                throw new CustomException(ErrorCode.INVALID_URL);
            }

            URL endpoint = new URL(storageEndpoint);
            if (!url.getHost().equals(endpoint.getHost())) {
                throw new CustomException(ErrorCode.INVALID_URL);
            }
            if (effectivePort(url) != effectivePort(endpoint)) {
                throw new CustomException(ErrorCode.INVALID_URL);
            }

            if (!url.getPath().startsWith("/" + bucketName + "/")) {
                throw new CustomException(ErrorCode.INVALID_URL);
            }
            return url;
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.INVALID_URL);
        }
    }

    private URLConnection openAllowedConnection(URL initialUrl) throws IOException {
        URL currentUrl = initialUrl;

        for (int redirectCount = 0; redirectCount <= MAX_REDIRECT_COUNT; redirectCount++) {
            parseAllowedDownloadUrl(currentUrl.toString());
            URLConnection connection = currentUrl.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);

            if (!(connection instanceof HttpURLConnection httpConnection)) {
                throw new CustomException(ErrorCode.INVALID_URL);
            }

            httpConnection.setInstanceFollowRedirects(false);
            int status = httpConnection.getResponseCode();
            if (isRedirect(status)) {
                String location = httpConnection.getHeaderField("Location");
                if (location == null || location.isBlank()) {
                    throw new CustomException(ErrorCode.INVALID_URL);
                }
                currentUrl = new URL(currentUrl, location);
                httpConnection.disconnect();
                continue;
            }

            return httpConnection;
        }

        throw new CustomException(ErrorCode.INVALID_URL);
    }

    private boolean isRedirect(int status) {
        return status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_SEE_OTHER
                || status == 307
                || status == 308;
    }

    private int effectivePort(URL url) {
        return url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
    }
}
