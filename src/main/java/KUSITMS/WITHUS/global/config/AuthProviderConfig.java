package KUSITMS.WITHUS.global.config;

import KUSITMS.WITHUS.global.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthProviderConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(BCryptPasswordEncoder encoder) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(encoder);
        p.setHideUserNotFoundExceptions(false);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoProvider) {
        // ProviderManager는 여러 AuthenticationProvider를 받을 수 있음.
        return new ProviderManager(List.of(daoProvider));
    }
}

