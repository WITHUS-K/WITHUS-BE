package KUSITMS.WITHUS.global.config;

import KUSITMS.WITHUS.global.auth.jwt.JwtFilter;
import KUSITMS.WITHUS.global.auth.jwt.LoginFilter;
import KUSITMS.WITHUS.global.auth.jwt.util.JwtUtil;
import KUSITMS.WITHUS.global.auth.service.AuthService;
import KUSITMS.WITHUS.global.auth.service.CustomUserDetailsService;
import KUSITMS.WITHUS.global.util.redis.RefreshTokenCacheUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final String[] allowedUrls = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/health",
            "/",
            "/api/v1/auth/**",
            "/api/v1/users/**",
            "/api/v1/organizations/search"
    };

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationManager authenticationManager;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final RefreshTokenCacheUtil refreshTokenCacheUtil;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 커스텀 프로바이더 등록
        http.authenticationProvider(daoAuthenticationProvider);

        LoginFilter loginFilter = new LoginFilter(authenticationManager, refreshTokenCacheUtil, jwtUtil, authService);
        loginFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(AbstractHttpConfigurer::disable);

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers(allowedUrls).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        //JWTFilter 등록
        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}