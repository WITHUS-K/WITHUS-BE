package KUSITMS.WITHUS.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 에러코드 규약
     * HTTP Status Code는 에러에 가장 유사한 코드를 부여한다.
     * 사용자정의 에러코드는 중복되지 않게 배정한다.
     * 사용자정의 에러코드는 각 카테고리 이름과 숫자를 조합하여 명확성을 더한다.
     * =============================================================
     * 400 : 잘못된 요청
     * 401 : 인증되지 않은 요청
     * 403 : 권한의 문제가 있을때
     * 404 : 요청한 리소스가 존재하지 않음
     * 409 : 현재 데이터와 값이 충돌날 때(ex. 아이디 중복)
     * 412 : 파라미터 값이 뭔가 누락됐거나 잘못 왔을 때
     * 422 : 파라미터 문법 오류
     * 424 : 뭔가 단계가 꼬였을때, 1번안하고 2번하고 그런경우
     * =============================================================
     */


    // Common
    SERVER_UNTRACKED_ERROR("COMMON500", "미등록 서버 에러입니다. 서버 팀에 연락주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST("COMMON400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("COMMON401", "인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("COMMON403", "권한이 부족합니다.", HttpStatus.FORBIDDEN),
    OBJECT_NOT_FOUND("COMMON404", "조회된 객체가 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PARAMETER("COMMON422", "잘못된 파라미터입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    PARAMETER_VALIDATION_ERROR("COMMON422", "파라미터 검증 에러입니다.", HttpStatus.UNPROCESSABLE_ENTITY),
    PARAMETER_GRAMMAR_ERROR("COMMON422", "파라미터 문법 에러입니다.", HttpStatus.UNPROCESSABLE_ENTITY),

    // Token
    TOKEN_INVALID("TOKEN401", "유효하지 않은 Token 입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_ROLE("TOKEN401", "JWT 토큰에 Role 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("TOKEN401", "Access Token 이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID("TOKEN401", "유효하지 않은 Access Token 입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("TOKEN404", "해당 사용자에 대한 Refresh Token 을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_MISMATCH("TOKEN401", "Refresh Token 이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("TOKEN401", "Refresh Token 이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("TOKEN401", "유효하지 않은 Refresh Token 입니다.", HttpStatus.UNAUTHORIZED),

    // User (회원)
    USER_ALREADY_EXIST("USER400", "이미 회원가입된 유저입니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST("USER404", "존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    USER_EMPLOYEE_ID_NOT_EXIST("USER404", "존재하지 않는 사번입니다.", HttpStatus.NOT_FOUND),
    USER_NOT_VALID("USER404", "유효한 사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_WRONG_PASSWORD("USER401", "비밀번호가 틀렸습니다.", HttpStatus.UNAUTHORIZED),
    USER_SAME_PASSWORD("USER400", "동일한 비밀번호로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    PASSWORDS_NOT_MATCH("PASSWORD401", "입력한 두 개의 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_NO_PERMISSION("USER403", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    USER_FORBIDDEN("USER403", "유저의 권한이 부족합니다.", HttpStatus.FORBIDDEN),

    // UserOrganization
    DUPLICATE_USER_ORGANIZATION("USER_ORGANIZATION400", "이미 조직에 가입된 사용자입니다.", HttpStatus.BAD_REQUEST),
    USER_ORGANIZATION_NOT_FOUND("USER_ORGANIZATION404", "해당 조직에서 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Organization (조직)
    ORGANIZATION_ALREADY_EXIST("ORGANIZATION400", "이미 존재하는 조직입니다.", HttpStatus.BAD_REQUEST),
    ORGANIZATION_NOT_EXIST("ORGANIZATION404", "존재하지 않는 조직입니다.", HttpStatus.NOT_FOUND),
    USER_NOT_IN_ORGANIZATION("ORGANIZATION404", "조직에 속해있지 않은 유저입니다.", HttpStatus.NOT_FOUND),

    // Recruitment (공고)
    RECRUITMENT_ALREADY_EXIST("RECRUITMENT400", "이미 존재하는 공고입니다.", HttpStatus.BAD_REQUEST),
    RECRUITMENT_NOT_EXIST("RECRUITMENT404", "존재하지 않는 공고입니다.", HttpStatus.NOT_FOUND),
    SLUG_GENERATION_FAILED("RECRUITMENT500", "슬러그 생성에 실패하였습니다. 슬러그 자릿수를 늘리세요.", HttpStatus.INTERNAL_SERVER_ERROR),

    // DocumentQuestion (지원서 서류 질문)
    DOCUMENT_QUESTION_ALREADY_EXIST("DOCUMENT_QUESTION400", "이미 존재하는 지원서 질문입니다.", HttpStatus.BAD_REQUEST),
    DOCUMENT_QUESTION_NOT_EXIST("DOCUMENT_QUESTION404", "존재하지 않는 지원서 질문입니다.", HttpStatus.NOT_FOUND),

    // Position (파트)
    POSITION_ALREADY_EXIST("POSITION400", "이미 해당 파트가 존재합니다.", HttpStatus.BAD_REQUEST),
    POSITION_NOT_EXIST("POSITION404", "존재하지 않는 파트입니다.", HttpStatus.NOT_FOUND),

    // Application (지원서)
    APPLICATION_ALREADY_EXIST("APPLICATION400", "이미 존재하는 지원서입니다.", HttpStatus.BAD_REQUEST),
    APPLICATION_NOT_EXIST("APPLICATION404", "존재하지 않는 지원서입니다.", HttpStatus.NOT_FOUND),
    REQUIRED_FIELD_MISSING("APPLICATION400", "필수 입력값이 누락되었습니다.", HttpStatus.BAD_REQUEST),

    // Interview (면접)
    INTERVIEW_ALREADY_EXIST("INTERVIEW400", "이미 존재하는 면접입니다.", HttpStatus.BAD_REQUEST),
    INTERVIEW_NOT_EXIST("INTERVIEW404", "존재하지 않는 면접입니다.", HttpStatus.NOT_FOUND),

    // TimeSlot (면접)
    TIME_SLOT_ALREADY_EXIST("TIME_SLOT400", "이미 존재하는 TimeSlot입니다.", HttpStatus.BAD_REQUEST),
    TIME_SLOT_NOT_EXIST("TIME_SLOT404", "존재하지 않는 TimeSlot입니다.", HttpStatus.NOT_FOUND),

    // EvaluationCriteria (평가 기준)
    EVALUATION_CRITERIA_ALREADY_EXIST("EVALUATION_CRITERIA400", "이미 존재하는 평가 기준입니다.", HttpStatus.BAD_REQUEST),
    EVALUATION_CRITERIA_NOT_EXIST("EVALUATION_CRITERIA404", "존재하지 않는 평가 기준입니다.", HttpStatus.NOT_FOUND),

    // Evaluation (평가)
    EVALUATION_ALREADY_EXIST("EVALUATION400", "이미 존재하는 평가입니다.", HttpStatus.BAD_REQUEST),
    EVALUATION_NOT_EXIST("EVALUATION404", "존재하지 않는 평가입니다.", HttpStatus.NOT_FOUND),

    // Role
    INVALID_ROLE("ROLE400", "잘못된 Role 값입니다.", HttpStatus.BAD_REQUEST),

    // OrganizationRole (조직 역할)
    ORGANIZATION_ROLE_ALREADY_EXIST("ORGANIZATION_ROLE400", "이미 추가된 역할입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_ORGANIZATION_ROLE_NAME("ORGANIZATION_ROLE400", "이미 존재하는 역할명입니다.", HttpStatus.BAD_REQUEST),
    ORGANIZATION_ROLE_NOT_EXIST("ORGANIZATION_ROLE404", "등록되지 않은 조직 역할입니다.", HttpStatus.NOT_FOUND),
    ORGANIZATION_ROLE_ORG_MISMATCH("ORGANIZATION_ROLE403", "해당 역할은 해당 조직에 속해 있지 않습니다.", HttpStatus.FORBIDDEN),

    // Verification
    VERIFICATION_EXPIRED("VERIFICATION404", "인증 코드가 만료되었습니다.", HttpStatus.NOT_FOUND),
    VERIFICATION_INVALID("VERIFICATION401", "유효하지 않은 인증 코드입니다.", HttpStatus.UNAUTHORIZED),
    VERIFICATION_NOT_EQUAL("VERIFICATION401", "인증 코드가 일차하지 않습니다.", HttpStatus.UNAUTHORIZED),
    NOT_VERIFIED("VERIFICATION403", "인증이 완료되지 않았습니다.", HttpStatus.FORBIDDEN),
    EMAIL_SEND_FAIL("EMAIL500", "메일 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_AUTH_FAIL("EMAIL401", "이메일 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_REQUEST_LIMIT_EXCEEDED("EMAIL429", "5분 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS),

    // File Upload
    FILE_UPLOAD_FAIL("FILE500", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_IO_ERROR("FILE500", "파일 처리 중 I/O 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_URL("FILE400", "잘못된 이미지 URL 형식입니다", HttpStatus.BAD_REQUEST),
    FILE_DELETE_FAIL("FILE500", "파일 삭제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}