package KUSITMS.WITHUS.domain.auth.service;

import KUSITMS.WITHUS.domain.auth.dto.JoinDTO;
import KUSITMS.WITHUS.domain.auth.enumerate.Role;
import KUSITMS.WITHUS.domain.user.entity.User;
import KUSITMS.WITHUS.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        Role role = joinDTO.getRole();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {

            return;
        }

        User data = new User();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole(role);

        userRepository.save(data);
    }
}
