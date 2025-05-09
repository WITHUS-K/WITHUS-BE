package KUSITMS.WITHUS.global.infra.email;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MailTemplateProvider {

    private final ResourceLoader resourceLoader;

    public String loadTemplate(MailTemplateType templateType, Map<String, String> variables) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/" + templateType.getFileName());
            String content = Files.readString(resource.getFile().toPath());

            for (Map.Entry<String, String> entry : variables.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return content;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.TEMPLATE_NOT_LOAD);
        }
    }
}
