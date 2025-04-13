package KUSITMS.WITHUS.domain.user.service;

import KUSITMS.WITHUS.domain.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.global.auth.enumerate.Role;
import KUSITMS.WITHUS.domain.user.entity.User;
import KUSITMS.WITHUS.domain.user.repository.UserRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(UserRequestDTO.Join request) {

        String email = request.email();
        String password = request.password();
        Role role = request.role();

        if (email == null || email.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        if (role == null) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }

        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }

        User data = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .role(role)
                .build();

        userRepository.save(data);
    }
}
