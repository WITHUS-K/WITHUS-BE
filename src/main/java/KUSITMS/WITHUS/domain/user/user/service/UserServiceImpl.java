package KUSITMS.WITHUS.domain.user.user.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.upload.service.FileUploadService;
import KUSITMS.WITHUS.global.util.redis.VerificationCacheUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@Builder
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final VerificationCacheUtil verificationCacheUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileUploadService uploadService;


    @Override
    public User getById(Long id) {
        return userRepository.getById(id);
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
     * 사용자 회원가입
     * @param email 사용자의 이메일
     * @param newPassword 새로 생성할 비밀번호
     * @throws CustomException 인증되지 않은 이메일이거나 유저를 찾을 수 없는 경우 예외를 발생시킵니다.
     */
    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        if (!verificationCacheUtil.isVerified(email)) {
            throw new CustomException(ErrorCode.NOT_VERIFIED);
        }

        User user = userRepository.getByEmail(email);

        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);
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


    /**
     * 이메일로 사용자 정보 조회
     * @param email 검색할 이메일 주소
     * @return 이메일로 조회된 사용자 정보
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepository.getByEmail(email);
    }


    /**
     * 회원가입 전 전화번호 인증 여부 확인
     * @param phoneNumber 확인할 전화번호
     * @throws CustomException 전화번호 인증이 완료되지 않은 경우 예외를 발생시킵니다.
     */
    public void checkPhoneVerifiedBeforeJoin(String phoneNumber) {
        if (!verificationCacheUtil.isVerified(phoneNumber)) {
            throw new CustomException(ErrorCode.NOT_VERIFIED);
        }
    }

    /**
     * 내 정보 조회
     * @param userId 현재 로그인 유저
     * @return 현재 유저 정보 반환
     */
    @Override
    public UserResponseDTO.MyPage getMyPage(Long userId) {
        User user = userRepository.getById(userId);
        return UserResponseDTO.MyPage.from(user);
    }

    /**
     * 내 정보 수정
     * @param request 수정할 정보를 담은 DTO
     * @param profileImage 수정할 프로필 이미지
     * @param user 현재 로그인 유저 정보
     * @return 수정된 정보 반환
     */
    @Override
    @Transactional
    public UserResponseDTO.MyPage updateUser(UserRequestDTO.Update request, MultipartFile profileImage, User user) {
        String encodedPassword = shouldUpdatePassword(request)
                ? processPasswordUpdate(request, user)
                : user.getPassword();

        // request에 비밀번호 값이 다 들어있을 때만 검증
        String imageUrl = user.getProfileImageUrl();
        if (profileImage != null && !profileImage.isEmpty()) {
            if (imageUrl != null) {
                uploadService.delete(imageUrl);
            }
            imageUrl = uploadService.uploadUserProfileImage(profileImage, user.getId());
        }

        user.update(request.name(), request.phoneNumber(), encodedPassword, imageUrl);

        return UserResponseDTO.MyPage.from(user);
    }

    private boolean shouldUpdatePassword(UserRequestDTO.Update request) {
        return isNotBlank(request.currentPassword()) &&
                isNotBlank(request.newPassword1()) &&
                isNotBlank(request.newPassword2());
    }

    private String processPasswordUpdate(UserRequestDTO.Update request, User user) {
        if (!bCryptPasswordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.USER_WRONG_PASSWORD);
        }
        if (!request.newPassword1().equals(request.newPassword2())) {
            throw new CustomException(ErrorCode.PASSWORDS_NOT_MATCH);
        }
        if (bCryptPasswordEncoder.matches(request.newPassword1(), user.getPassword())) {
            throw new CustomException(ErrorCode.USER_SAME_PASSWORD);
        }
        return bCryptPasswordEncoder.encode(request.newPassword1());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
