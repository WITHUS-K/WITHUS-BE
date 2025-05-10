package KUSITMS.WITHUS.global.infra.email.template;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MailTemplateProvider {

    private final ResourceLoader resourceLoader;

    public String loadTemplate(MailTemplateType templateType, Map<String, String> variables) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/" + templateType.getFileName());
            try (InputStream is = resource.getInputStream()) {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
                }

                return content;
            }
        } catch (IOException e) {
            throw new CustomException(ErrorCode.TEMPLATE_NOT_LOAD);
        }
    }
}
