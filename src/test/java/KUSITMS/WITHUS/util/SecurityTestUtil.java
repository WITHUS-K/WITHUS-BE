package KUSITMS.WITHUS.util;

import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.auth.dto.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityTestUtil {

    public static void setAuthentication(User user) {
        UserDetails userDetails = new CustomUserDetails(user);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
    }
}
