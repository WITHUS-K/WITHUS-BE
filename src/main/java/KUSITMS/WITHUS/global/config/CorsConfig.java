package KUSITMS.WITHUS.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("http://localhost:3000");
        config.addAllowedOriginPattern("http://localhost:8080");
        config.addAllowedOriginPattern("https://jk-project.site");
        config.addAllowedOriginPattern("https://www.jk-project.site");
        config.addAllowedOriginPattern("https://withus-ten.vercel.app");
        config.addAllowedOriginPattern("https://recruit-withus.co.kr");
        config.addAllowedOriginPattern("https://www.recruit-withus.co.kr");
        for (int port = 3000; port <= 3010; port++) {
            config.addAllowedOriginPattern("http://localhost:" + port);
        }

        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Refresh-Token");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
