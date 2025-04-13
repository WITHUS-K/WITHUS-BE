package KUSITMS.WITHUS.global.auth.service;

import KUSITMS.WITHUS.global.auth.dto.JoinDTO;
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

    public void joinProcess(JoinDTO joinDTO) {

        String email = joinDTO.email();
        String password = joinDTO.password();
        Role role = joinDTO.role();

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
