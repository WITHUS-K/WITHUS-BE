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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
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

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
