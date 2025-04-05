package KUSITMS.WITHUS.global.response;

import KUSITMS.WITHUS.global.response.result.ResponseState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SuccessResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private final boolean isSuccess = true;

    @Schema(description = "상태 코드", example = "1")
    private final int code;

    @Schema(description = "응답 메세지", example = "성공하였습니다.")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T result;

    public SuccessResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> SuccessResponse<T> of(int code, String message, T data) {
        return new SuccessResponse<>(code, message, data);
    }

    public static <T> SuccessResponse<T> ok(T data) {
        return of(ResponseState.SUCCESS.getCode(), ResponseState.SUCCESS.getMessage(), data);
    }
}
