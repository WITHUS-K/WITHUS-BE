package KUSITMS.WITHUS.global.infra.upload.uploader;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NcpObjectUploader implements Uploader {

    private final S3Client s3Client;

    @Value("${ncp.storage.bucket-name}")
    private String bucketName;

    @Value("${ncp.storage.endpoint}")
    private String endpoint;

    @Override
    public String upload(MultipartFile file, String pathPrefix) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String key = (pathPrefix != null ? pathPrefix : "") + fileName;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            if (response.sdkHttpResponse().isSuccessful()) {
                return String.format("%s/%s/%s", endpoint, bucketName, key);
            } else {
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
            }

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_IO_ERROR);
        }
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(builder -> builder
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAIL);
        }
    }
}
