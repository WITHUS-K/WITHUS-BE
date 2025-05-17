package KUSITMS.WITHUS.global.auth.jwt;

import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;
import KUSITMS.WITHUS.global.auth.dto.CustomUserDetails;
import KUSITMS.WITHUS.global.auth.jwt.util.JwtUtil;
import KUSITMS.WITHUS.global.auth.service.AuthService;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.response.SuccessResponse;
import KUSITMS.WITHUS.global.util.redis.RefreshTokenCacheUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

import static java.util.Map.of;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenCacheUtil refreshTokenCacheUtil;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        // 기본은 request.getParameter("username") → email로 바꿔서 클라이언트에서 email, password로 요청 보낼 수 있게끔 수정
        return request.getParameter("email");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON -> Java 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            UserRequestDTO.Login loginRequest = objectMapper.readValue(request.getInputStream(), UserRequestDTO.Login.class);

            //클라이언트 요청에서 username, password 추출
            String email = loginRequest.email();
            String password = loginRequest.password();

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)  throws IOException {

        //UserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // Spring Security의 필수 인터페이스가 username으로 되어있어서 이 부분은 username으로 남김
        String email = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email, role);

        // Redis에 Refresh Token 저장
        Duration refreshTokenTTL = Duration.ofDays(7);
        refreshTokenCacheUtil.saveRefreshToken(email, refreshToken, refreshTokenTTL);

        // 응답으로 AccessToken + RefreshToken 내려주기
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh-Token", refreshToken);

        response.setContentType("application/json;charset=UTF-8");

        UserResponseDTO.Login loginDto = authService.loginDtoByEmail(email);

        new ObjectMapper().writeValue(response.getWriter(), SuccessResponse.ok(loginDto));

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper om = new ObjectMapper();

        if (failed instanceof UsernameNotFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            om.writeValue(response.getWriter(), of(
                    "code", ErrorCode.USER_NOT_EXIST.getErrorCode(),
                    "message",   "가입된 이메일이 존재하지 않습니다. 다시 입력해주세요.",
                    "result",   "",
                    "success",  false
            ));
        }
        else if (failed instanceof BadCredentialsException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            om.writeValue(response.getWriter(), of(
                    "code", ErrorCode.USER_WRONG_PASSWORD.getErrorCode(),
                    "message",   "비밀번호가 일치하지 않습니다. 다시 입력해주세요.",
                    "result",   "",
                    "success",  false
            ));
        }
        else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            om.writeValue(response.getWriter(), of(
                    "errorCode", ErrorCode.UNAUTHORIZED.getErrorCode(),
                    "message",   "인증에 실패했습니다.",
                    "result",   "",
                    "success",  false
            ));
        }
    }
}