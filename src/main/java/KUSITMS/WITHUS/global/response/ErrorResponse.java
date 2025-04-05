package KUSITMS.WITHUS.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse<T> {

    @Schema(description = "성공 여부", example = "false")
    private final boolean isSuccess = false;

    @Schema(description = "예외 코드", example = "COMMON500")
    private final String code;

    @Schema(description = "예외 메세지", example = "실패하였습니다.")
    private final String message;

    @Schema(description = "예외 참고 데이터")
    private final T result;

    public ErrorResponse(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> ErrorResponse<T> of(String code, String message) {
        return new ErrorResponse<>(code, message, null);
    }

    public static <T> ErrorResponse<T> ok(String code, String message, T data) {
        return new ErrorResponse<>(code, message, data);
    }
}
