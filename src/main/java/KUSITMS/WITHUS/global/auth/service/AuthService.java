package KUSITMS.WITHUS.global.auth.service;

public interface AuthService {
    String reissueAccessToken(String refreshToken);
    void logout();

    void requestEmailVerification(String name, String email);

    void requestPhoneVerification(String phoneNumber);

    void confirmVerification(String identifier, String inputCode);
}
