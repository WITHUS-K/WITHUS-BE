package KUSITMS.WITHUS.domain.user.user.service;

import KUSITMS.WITHUS.domain.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.global.auth.dto.CustomUserDetails;
import KUSITMS.WITHUS.global.auth.jwt.util.JwtUtil;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.sms.SmsSender;
import KUSITMS.WITHUS.global.util.redis.RefreshTokenCacheUtil;
import KUSITMS.WITHUS.global.util.redis.VerificationCacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final VerificationCacheUtil verificationCacheUtil;
    private final SmsSender smsSender;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenCacheUtil refreshTokenCacheUtil;

    private static final Duration CODE_TTL = Duration.ofMinutes(5); // 인증코드 유효기간 5분
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(10); // 인증완료 상태 유지 10분

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
     * 관리자 회원가입
     * @param request 관리자의 이름, 조직명, 이메일, 비밀번호, 전화번호를 입력받습니다.
     * @throws CustomException 이메일이 이미 존재하거나 전화번호 인증이 완료되지 않은 경우 예외를 발생시킵니다.
     */
    @Override
    @Transactional
    public void adminJoinProcess(UserRequestDTO.AdminJoin request) {

        String name = request.name();
        String organizationName = request.organizationName();
        String email = request.email();
        String password = request.password();
        String phoneNumber = request.phoneNumber();

        // 이미 존재하는 사용자인지 확인
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }

        // 같은 이름의 조직이 존재하는지 확인
        if (organizationRepository.existsByName(organizationName)) {
            throw new CustomException(ErrorCode.ORGANIZATION_ALREADY_EXIST);
        }

        // 휴대폰 번호 인증 여부 확인
        checkPhoneVerifiedBeforeJoin(phoneNumber);

        // Organization 생성 및 저장
        Organization organization = organizationRepository.save(
                Organization.create(organizationName)
        );

        // User 생성
        User user = User.builder()
                .name(name)
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .role(Role.ADMIN)
                .gender(Gender.NONE)
                .build();

        // UserOrganization 생성 및 연관관계 설정
        UserOrganization userOrg = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .build();

        // 연관관계 등록
        user.addUserOrganization(userOrg);
        organization.addUserOrganization(userOrg);

        userRepository.save(user);
    }

    /**
     * 사용자 회원가입
     * @param request 사용자의 이름, 생년월일, 성별, 조직 ID, 이메일, 비밀번호, 전화번호를 입력받습니다.
     * @throws CustomException 이메일이 이미 존재하거나 전화번호 인증이 완료되지 않은 경우 예외를 발생시킵니다.
     */
    @Override
    @Transactional
    public void userJoinProcess(UserRequestDTO.UserJoin request) {

        String name = request.name();
        LocalDate birthDate = request.birthDate();
        Gender gender = request.gender();
        Long organizationId = request.organizationId();
        String email = request.email();
        String password = request.password();
        String phoneNumber = request.phoneNumber();

        // 이미 존재하는 사용자인지 확인
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }

        // 휴대폰 번호 인증 여부 확인
        checkPhoneVerifiedBeforeJoin(phoneNumber);

        // 존재하는 조직 Id로 조회
        Organization organization = organizationRepository.getById(organizationId);

        // User 생성
        User user = User.builder()
                .name(name)
                .birthDate(birthDate)
                .gender(gender)
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();

        // UserOrganization 생성 및 연관관계 설정
        UserOrganization userOrg = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .build();

        // 연관관계 등록
        user.addUserOrganization(userOrg);
        organization.addUserOrganization(userOrg);

        userRepository.save(user);
    }

    /**
     * 이메일 중복 확인
     * @param email 확인할 이메일 주소
     * @return 이메일 중복 여부(true/false)
     */
    @Override
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void requestEmailVerification(String name, String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }
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
     * 전화번호 인증 확인
     * @param phoneNumber 인증할 전화번호
     * @param inputCode 사용자가 입력한 인증 코드
     * @throws CustomException 잘못된 인증 코드인 경우 예외를 발생시킵니다.
     */
    @Override
    public void confirmPhoneVerification(String phoneNumber, String inputCode) {
        String savedCode = verificationCacheUtil.getCode(phoneNumber);

        if (!inputCode.equals(savedCode)) {
            throw new CustomException(ErrorCode.INVALID_PHONE_VERIFICATION_CODE);
        }

        verificationCacheUtil.markVerified(phoneNumber, VERIFIED_TTL);
    }

    /**
     * 이메일로 사용자 정보 조회
     * @param email 검색할 이메일 주소
     * @return 이메일로 조회된 사용자 정보
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 회원가입 전 전화번호 인증 여부 확인
     * @param phoneNumber 확인할 전화번호
     * @throws CustomException 전화번호 인증이 완료되지 않은 경우 예외를 발생시킵니다.
     */
    public void checkPhoneVerifiedBeforeJoin(String phoneNumber) {
        if (!verificationCacheUtil.isVerified(phoneNumber)) {
            throw new CustomException(ErrorCode.PHONE_NOT_VERIFIED);
        }
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
        System.out.println("[이메일 인증] " + email + " → 인증코드: " + code);
    }
}
