package KUSITMS.WITHUS.global.infra.upload.dto;

public class FileResponseDTO {

    public record Upload(
            String fileName,
            String url,
            long size
    ) {
        public static FileResponseDTO.Upload from(String fileName, String url, long size) {
            return new FileResponseDTO.Upload(
                    fileName, url, size
            );
        }
    }
}
