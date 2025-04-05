package KUSITMS.WITHUS.global.exception;

import KUSITMS.WITHUS.global.response.ErrorResponse;
import KUSITMS.WITHUS.global.response.result.ExceptionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static KUSITMS.WITHUS.global.exception.ErrorCode.PARAMETER_GRAMMAR_ERROR;
import static KUSITMS.WITHUS.global.exception.ErrorCode.PARAMETER_VALIDATION_ERROR;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 등록되지 않은 에러
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse<ExceptionResult.ServerErrorData> handleUntrackedException(Exception e) {
        ExceptionResult.ServerErrorData serverErrorData = ExceptionResult.ServerErrorData.builder()
                .errorClass(e.getClass().toString())
                .errorMessage(e.getMessage())
                .build();
        return ErrorResponse.ok(ErrorCode.SERVER_UNTRACKED_ERROR.getErrorCode(), ErrorCode.SERVER_UNTRACKED_ERROR.getMessage(), serverErrorData);
    }

    /**
     * 파라미터 검증 예외
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ErrorResponse<List<ExceptionResult.ParameterData>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ExceptionResult.ParameterData> list = new ArrayList<>();

        BindingResult bindingResult = e.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            ExceptionResult.ParameterData parameterData = ExceptionResult.ParameterData.builder()
                    .key(fieldError.getField())
                    .value(fieldError.getRejectedValue() == null ? null : fieldError.getRejectedValue().toString())
                    .reason(fieldError.getDefaultMessage())
                    .build();
            list.add(parameterData);
        }

        return ErrorResponse.ok(PARAMETER_VALIDATION_ERROR.getErrorCode(), PARAMETER_VALIDATION_ERROR.getMessage(), list);
    }


    /**
     * 파라미터 문법 예외
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ErrorResponse<String> handleHttpMessageParsingExceptions(HttpMessageNotReadableException e) {
        return ErrorResponse.ok(PARAMETER_GRAMMAR_ERROR.getErrorCode(), PARAMETER_GRAMMAR_ERROR.getMessage(), e.getMessage());
    }

    /**
     * 커스텀 예외
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse<?>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse<?> errorResponse = ErrorResponse.of(
                errorCode.getErrorCode(),
                errorCode.getMessage()
        );

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
