package KUSITMS.WITHUS.global.auth.service;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //DB에서 조회
        User userData = userRepository.findByEmail(email);

        if (userData == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        //UserDetails에 담아서 return하면 AutneticationManager가 검증함
        return new CustomUserDetails(userData);
    }
}
