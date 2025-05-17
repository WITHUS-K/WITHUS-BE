package KUSITMS.WITHUS.global.auth.service;

import KUSITMS.WITHUS.domain.user.user.dto.UserResponseDTO;

public interface AuthService {

    UserResponseDTO.Login loginDtoByEmail(String email);

    String reissueAccessToken(String refreshToken);

    void logout();

    void requestEmailVerification(String name, String email);

    void requestPhoneVerification(String phoneNumber);

    void confirmVerification(String identifier, String inputCode);
}
