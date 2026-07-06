package KUSITMS.WITHUS.global.infra.email.delivery;

import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import KUSITMS.WITHUS.global.infra.email.MailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class SendGridEventWebhookVerifier {

    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String KEY_ALGORITHM = "EC";

    private final MailProperties mailProperties;

    public void verify(String signature, String timestamp, byte[] payload) {
        if (!StringUtils.hasText(signature)
                || !StringUtils.hasText(timestamp)
                || !StringUtils.hasText(mailProperties.getSendgridEventWebhookPublicKey())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        try {
            Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifier.initVerify(publicKey());
            verifier.update(timestamp.getBytes(StandardCharsets.UTF_8));
            verifier.update(payload);

            if (!verifier.verify(Base64.getDecoder().decode(signature))) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    private PublicKey publicKey() throws Exception {
        String key = mailProperties.getSendgridEventWebhookPublicKey()
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decodedKey));
    }
}
