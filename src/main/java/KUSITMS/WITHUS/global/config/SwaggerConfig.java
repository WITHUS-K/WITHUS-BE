package KUSITMS.WITHUS.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement());
    }

    private Info apiInfo() {
        return new Info()
                .title("WITHUS Backend API Docs")
                .description("WITHUS API 명세서입니다.");
    }

    // JWT 토큰을 위한 보안 컴포넌트 설정
    private Components components() {
        String securityScheme = "JWT TOKEN";
        return new Components()
                .addSecuritySchemes(securityScheme, new SecurityScheme()
                        .name(securityScheme)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT"));
    }

    // 보안 요구사항 설정
    private SecurityRequirement securityRequirement() {
        String securityScheme = "JWT TOKEN";
        return new SecurityRequirement().addList(securityScheme);
    }
}
