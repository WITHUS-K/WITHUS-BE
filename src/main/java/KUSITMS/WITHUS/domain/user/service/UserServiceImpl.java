package KUSITMS.WITHUS.domain.user.service;

import KUSITMS.WITHUS.domain.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.entity.User;
import KUSITMS.WITHUS.domain.user.repository.UserRepository;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.util.redis.PhoneAuthCacheUtil;
import lombok.RequiredArgsConstructor;
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
    private final PhoneAuthCacheUtil phoneAuthCacheUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    public void requestPhoneVerification(String phoneNumber) {
        String code = generateCode(); // 랜덤 인증번호 생성
        phoneAuthCacheUtil.saveCode(phoneNumber, code, Duration.ofMinutes(3));

        // 실제 SMS 전송 로직 (예: coolSMS, 알리고 등)
        sendSms(phoneNumber, code);
    }

    public void confirmPhoneVerification(String phoneNumber, String inputCode) {
        String savedCode = phoneAuthCacheUtil.getCode(phoneNumber);

        if (!inputCode.equals(savedCode)) {
            throw new CustomException(ErrorCode.INVALID_PHONE_VERIFICATION_CODE);
        }

        phoneAuthCacheUtil.markVerified(phoneNumber, Duration.ofMinutes(5));
    }

    public void checkPhoneVerifiedBeforeJoin(String phoneNumber) {
        if (!phoneAuthCacheUtil.isVerified(phoneNumber)) {
            throw new CustomException(ErrorCode.PHONE_NOT_VERIFIED);
        }
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000); // 6자리 랜덤
    }

    private void sendSms(String phoneNumber, String code) {
        // TODO: 문자 발송 API 연동
        System.out.println("📲 [" + phoneNumber + "]에게 인증번호 [" + code + "] 발송됨!");
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
