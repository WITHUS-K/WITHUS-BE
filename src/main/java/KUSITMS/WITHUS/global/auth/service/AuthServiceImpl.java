package KUSITMS.WITHUS.global.auth.service;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.auth.dto.CustomUserDetails;
import KUSITMS.WITHUS.global.auth.jwt.util.JwtUtil;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import KUSITMS.WITHUS.global.infra.email.sender.MailSender;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateProvider;
import KUSITMS.WITHUS.global.infra.email.template.MailTemplateType;
import KUSITMS.WITHUS.global.infra.sms.SmsSender;
import KUSITMS.WITHUS.global.util.redis.RefreshTokenCacheUtil;
import KUSITMS.WITHUS.global.util.redis.VerificationCacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenCacheUtil refreshTokenCacheUtil;
    private final VerificationCacheUtil verificationCacheUtil;
    private final SmsSender smsSender;
    private final MailSender mailSender;
    private final MailTemplateProvider templateProvider;
    private final MailProperties mailProperties;

    private static final Duration CODE_TTL = Duration.ofMinutes(5); // 인증코드 유효기간 5분
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(10); // 인증완료 상태 유지 10분

    /**
     * Refresh Token을 검증하고, 유효한 경우 새로운 Access Token을 발급합니다.
     * @param refreshToken 클라이언트로부터 전달받은 Refresh Token
     * @return 새로 발급된 Access Token
     * @throws CustomException Refresh Token이 만료되었거나 유효하지 않은 경우 예외를 발생시킵니다.
     */
    @Override
    public String reissueAccessToken(String refreshToken) {
        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String email = jwtUtil.getEmail(refreshToken);

        // Redis에 저장된 Refresh Token과 비교
        String storedRefreshToken = refreshTokenCacheUtil.getRefreshToken(email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String role = jwtUtil.getRole(refreshToken);

        return jwtUtil.createAccessToken(email, role);
    }

    /**
     * 로그아웃
     * @throws CustomException 인증 정보가 없거나 유효하지 않은 경우 예외를 발생시킵니다.
     */
    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 인증이 안 된 경우 (토큰이 없을 때 포함)
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        refreshTokenCacheUtil.deleteRefreshToken(email);
    }

    /**
     * 이메일 인증 요청
     * @param name 사용자의 이름
     * @param email 사용자의 이메일
     * @throws CustomException 사용자를 찾을 수 없거나, 사용자의 이름이 일치하지 않는 경우 예외를 발생시킵니다.
     */
    @Override
    public void requestEmailVerification(String name, String email) {
        User user = userRepository.getByEmail(email);
        if (!user.getName().equals(name)) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }

        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        verificationCacheUtil.saveCode(email, code, CODE_TTL);

        sendEmail(email, code);

    }

    /**
     * 전화번호 인증 요청
     * @param phoneNumber 인증 번호를 보낼 전화번호
     */
    @Override
    public void requestPhoneVerification(String phoneNumber) {
        String code = generateCode(); // 랜덤 인증번호 생성
        verificationCacheUtil.saveCode(phoneNumber, code, CODE_TTL);

        sendSms(phoneNumber, code);
    }

    /**
     * 인증 확인
     * @param identifier 인증할 식별자 (전화번호/이메일)
     * @param inputCode 사용자가 입력한 인증 코드
     * @throws CustomException 잘못된 인증 코드인 경우 예외를 발생시킵니다.
     */
    @Override
    public void confirmVerification(String identifier, String inputCode) {
        String savedCode = verificationCacheUtil.getCode(identifier);

        if (savedCode == null) {
            throw new CustomException(ErrorCode.VERIFICATION_INVALID);
        }
        if (!inputCode.equals(savedCode)) {
            throw new CustomException(ErrorCode.VERIFICATION_NOT_EQUAL);
        }

        verificationCacheUtil.markVerified(identifier, VERIFIED_TTL);
    }

    /**
     * 랜덤 인증 코드 생성
     * @return 6자리 랜덤 인증 코드
     */
    private String generateCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000); // 6자리 랜덤
    }

    /**
     * 문자 메시지 전송
     * @param phoneNumber 메시지를 보낼 전화번호
     * @param code 인증 코드
     */
    private void sendSms(String phoneNumber, String code) {
        String message = "[WITHUS] 인증번호 [" + code + "]를 입력해주세요.";
        smsSender.send(phoneNumber, message);
    }

    /**
     * 이메일 전송
     * @param email 메시지를 보낼 이메일 주소
     * @param code 인증 코드
     */
    private void sendEmail(String email, String code) {
        Map<String, String> variables = Map.of(
                "logoUrl", mailProperties.getLogoUrl(),
                "code", code
        );

        String html = templateProvider.loadTemplate(MailTemplateType.VERIFICATION, variables);
        String subject = "[WITHUS] 비밀번호 재설정 인증 번호 발송 메일입니다.";

        mailSender.send(email, subject, html);
    }

}
